package com.hescul.urgent.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.hescul.urgent.R
import com.hescul.urgent.UrgentViewModels
import com.hescul.urgent.core.auth.cognito.CognitoAuthenticator
import com.hescul.urgent.ui.screens.confirm.ConfirmScreen
import com.hescul.urgent.ui.screens.home.HomeScreen
import com.hescul.urgent.ui.screens.login.LoginScreen
import com.hescul.urgent.ui.screens.signup.SignUpScreen
import com.hescul.urgent.ui.theme.SystemTheme
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UrgentNavHost(
    startDestination: String,
    cognito: CognitoAuthenticator,
    viewModels: UrgentViewModels,
    modifier: Modifier = Modifier
) {
    UrgentTheme {
        //val allScreens = UrgentScreen.values().toList()
        val navController = rememberAnimatedNavController()
        SystemTheme()
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                // Sign Up Screen
                composable(
                    route = UrgentScreen.SignUp.name,
                    enterTransition = {
                        when (initialState.destination.route) {
                            UrgentScreen.Login.name -> slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                            else -> EnterTransition.None
                        }
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(500)
                        )
                    }
                ) {
                    val defaultFailCause = stringResource(id = R.string.ui_signUpScreen_defaultFailCause)
                    val signUpViewModel = viewModels.signUpViewModel
                    SignUpScreen(
                        signUpViewModel = signUpViewModel,
                        onSignUpRequest = {
                            cognito.signUpUser(
                                name = signUpViewModel.nameTextInput,
                                userId = signUpViewModel.emailTextInput,
                                password = signUpViewModel.passwordTextInput,
                                onSignUpSuccess = { user, signUpResult ->
                                    signUpViewModel.onSignUpSuccess()
                                    if (!signUpResult.isUserConfirmed) { // navigate to confirm screen
                                        viewModels.confirmViewModel.reset()
                                        viewModels.confirmViewModel.updateConfirmationIdentity(
                                            userId = user.userId,
                                            medium = signUpResult.codeDeliveryDetails.deliveryMedium,
                                            destination = signUpResult.codeDeliveryDetails.destination
                                        )
                                        navController.navigate(UrgentScreen.Confirm.name) {

                                        }
                                    }
                                    else {  // back to login
                                        viewModels.loginViewModel.reset()
                                        navController.popBackStack()
                                    }
                                },
                                onSignUpFailure = { cause ->
                                    signUpViewModel.onSignUpFailure(CognitoAuthenticator.reduceFailCause(cause))
                                },
                                defaultFailCause = defaultFailCause
                            )
                        }
                    )
                }

                // Confirm Screen
                composable(
                    route = UrgentScreen.Confirm.name,
                    enterTransition = { EnterTransition.None }
                ) {
                    val defaultFailCause = stringResource(id = R.string.ui_confirmScreen_defaultFailCause)
                    val confirmViewModel = viewModels.confirmViewModel
                    ConfirmScreen(
                        confirmViewModel = confirmViewModel,
                        confirmMedium = confirmViewModel.medium,
                        confirmDestination = confirmViewModel.destination,
                        onConfirmRequest = {
                            // This will cause confirmation to fail if the user attribute
                            // has been verified for another user in the same pool
                            val forcedAliasCreation = false
                            cognito.confirmSignUp(
                                userId = confirmViewModel.userId,
                                confirmationCode = confirmViewModel.confirmCodeTextInput,
                                forcedAliasCreation = forcedAliasCreation,
                                onConfirmSuccess = confirmViewModel::onConfirmSuccess,
                                onConfirmFailure = { cause ->
                                    confirmViewModel.onConfirmFailure(CognitoAuthenticator.reduceFailCause(cause))
                                },
                                defaultFailCause = defaultFailCause
                            )
                        },
                        onBackToLogInPressed = {
                            viewModels.loginViewModel.reset()
                            navController.navigate(UrgentScreen.Login.name)
                        }
                    )
                }

                // Login Screen
                composable(
                    route = UrgentScreen.Login.name,
                    enterTransition = {
                        fadeIn(animationSpec = tween(durationMillis = 700))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(durationMillis = 500))
                    },
                ) {
                    val defaultFailCause = stringResource(id = R.string.ui_loginScreen_defaultFailCause)
                    val loginViewModel = viewModels.loginViewModel
                    LoginScreen(
                        loginViewModel = loginViewModel,
                        onLoginRequest = {
                            cognito.loginUser(
                                userId = loginViewModel.emailTextInput,
                                onLoginSuccess = {
                                    navController.navigate(UrgentScreen.Home.name) {
                                        popUpTo(UrgentScreen.Login.name) { inclusive = true }
                                    }
                                },
                                onLoginFailure = { cause ->
                                    loginViewModel.onLoginFailure(CognitoAuthenticator.reduceFailCause(cause))
                                },
                                onAuthenticationDetailsRequest = { continuation, _ ->
                                    if (continuation == null) {
                                        loginViewModel.onLoginFailure(defaultFailCause)
                                    }
                                    else {
                                        val authenticationDetails = AuthenticationDetails(
                                            loginViewModel.emailTextInput,
                                            loginViewModel.passwordTextInput,
                                            null,
                                        )
                                        continuation.setAuthenticationDetails(authenticationDetails)
                                        continuation.continueTask()
                                    }
                                },
                                onMFACodeRequest = {

                                },
                                onAuthChallengeRequest = {

                                },
                                defaultFailCause = defaultFailCause
                            )
                        },
                        onNavigateToSignUp = {
                            viewModels.signUpViewModel.reset()
                            navController.navigate(UrgentScreen.SignUp.name)
                        },
                        onReconfirmRequest = {
                            cognito.resendSignUpConfirmation(
                                userId = loginViewModel.emailTextInput,
                                onResendConfirmationSuccess = { verificationCodeDeliveryMedium ->
                                    viewModels.confirmViewModel.reset()
                                    viewModels.confirmViewModel.updateConfirmationIdentity(
                                        userId = loginViewModel.emailTextInput,
                                        medium = verificationCodeDeliveryMedium.deliveryMedium,
                                        destination = verificationCodeDeliveryMedium.destination
                                    )
                                    navController.navigate(UrgentScreen.Confirm.name)
                                },
                                onResendConfirmationFailure = { cause ->
                                    loginViewModel.onLoginFailure(CognitoAuthenticator.reduceFailCause(cause))
                                },
                            )
                        }
                    )
                }

                // Home Screen
                composable(
                    route = UrgentScreen.Home.name,
                    enterTransition = { EnterTransition.None }
                ) {
                    HomeScreen()
                }
            }
        }
    }
}