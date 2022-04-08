package com.hescul.urgent.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.hescul.urgent.UrgentViewModels
import com.hescul.urgent.ui.screens.confirm.ConfirmScreen
import com.hescul.urgent.ui.screens.home.HomeScreen
import com.hescul.urgent.ui.screens.login.LoginScreen
import com.hescul.urgent.ui.screens.opening.OpeningScreen
import com.hescul.urgent.ui.screens.signup.SignUpScreen
import com.hescul.urgent.ui.theme.SystemTheme
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UrgentNavHost(
    startDestination: String,
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
                // Opening Screen
                composable(
                    route = UrgentScreen.Opening.name,
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(200)) }
                ) {
                    val openingViewModel = viewModels.openingViewModel
                    OpeningScreen(
                        openingViewModel = openingViewModel,
                        onDone = {
                            navController.navigate(UrgentScreen.Login.name) {
                                popUpTo(UrgentScreen.Opening.name) { inclusive = true }
                            }
                        }
                    )
                }

                // Sign Up Screen
                composable(
                    route = UrgentScreen.SignUp.name,
                    enterTransition = {
                        when (initialState.destination.route) {
                            UrgentScreen.Login.name -> slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(durationMillis = NavConfig.ENTER_TRANSITION_DURATION)
                            )
                            else -> fadeIn(animationSpec = tween(durationMillis = NavConfig.ENTER_TRANSITION_DURATION))
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            UrgentScreen.Login.name -> slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(durationMillis = NavConfig.EXIT_TRANSITION_DURATION)
                            )
                            else -> fadeOut(animationSpec = tween(durationMillis = NavConfig.EXIT_TRANSITION_DURATION))
                        }
                    }
                ) {
                    val signUpViewModel = viewModels.signUpViewModel
                    SignUpScreen(
                        signUpViewModel = signUpViewModel,
                        onSignUpDone = { cognitoUser, signUpResult ->
                            viewModels.confirmViewModel.updateConfirmationIdentity(
                                userId = cognitoUser.userId,
                                medium = signUpResult.codeDeliveryDetails.deliveryMedium,
                                destination = signUpResult.codeDeliveryDetails.destination
                            )
                            viewModels.confirmViewModel.reset()
                            navController.navigate(UrgentScreen.Confirm.name)
                        },
                        onNavigateBack = {
                            viewModels.loginViewModel.reset()
                            navController.popBackStack()
                        }
                    )
                }

                // Confirm Screen
                composable(
                    route = UrgentScreen.Confirm.name,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(durationMillis = NavConfig.ENTER_TRANSITION_DURATION)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(durationMillis = NavConfig.EXIT_TRANSITION_DURATION + 200)
                        )
                    }
                ) {
                    val confirmViewModel = viewModels.confirmViewModel
                    ConfirmScreen(
                        confirmViewModel = confirmViewModel,
                        onBackToLogIn = {
                            viewModels.loginViewModel.reset()
                            navController.navigate(UrgentScreen.Login.name) {
                                popUpTo(UrgentScreen.Login.name) { inclusive = true }
                            }
                        }
                    )
                }

                // Login Screen
                composable(
                    route = UrgentScreen.Login.name,
                    enterTransition = {
                        fadeIn(animationSpec = tween(durationMillis = NavConfig.ENTER_TRANSITION_DURATION))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(durationMillis = NavConfig.EXIT_TRANSITION_DURATION))
                    },
                ) {
                    val loginViewModel = viewModels.loginViewModel
                    LoginScreen(
                        loginViewModel = loginViewModel,
                        onLoginDone = { userSession -> // TODO
                            navController.navigate(UrgentScreen.Home.name) {
                                popUpTo(UrgentScreen.Login.name) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = {
                            viewModels.signUpViewModel.reset()
                            navController.navigate(UrgentScreen.SignUp.name)
                        },
                        onResendSignUpConfirmationDone = { userId, codeDeliveryDetails ->
                            viewModels.confirmViewModel.reset()
                            viewModels.confirmViewModel.updateConfirmationIdentity(
                                userId = userId,
                                medium = codeDeliveryDetails.deliveryMedium,
                                destination = codeDeliveryDetails.destination
                            )
                            navController.navigate(UrgentScreen.Confirm.name)
                        }
                    )
                }

                // Home Screen
                composable(
                    route = UrgentScreen.Home.name,
                    enterTransition = { EnterTransition.None }
                ) {
                    val homeViewModel = viewModels.homeViewModel
                    HomeScreen(
                        homeViewModel = homeViewModel,
                    )
                }
            }
        }
    }
}