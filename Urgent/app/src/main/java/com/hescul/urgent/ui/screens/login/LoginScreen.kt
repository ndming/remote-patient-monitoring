package com.hescul.urgent.ui.screens.login

import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.hescul.urgent.R
import com.hescul.urgent.ui.screens.signup.EmailField
import com.hescul.urgent.ui.screens.signup.PasswordField
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.LoadingButton
import com.hescul.urgent.ui.versatile.UrgentTopBar
import com.hescul.urgent.ui.versatile.config.LoadingButtonConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginDone: (CognitoUserSession) -> Unit,
    onResendSignUpConfirmationDone: (String, CognitoUserCodeDeliveryDetails) -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current
    val innerPadding = 5.dp
    val contentPadding = 40.dp
    Column(modifier = modifier) {
        UrgentTopBar(
            title = stringResource(id = R.string.ui_loginScreen_title),
            showNavigateBack = false,
            showRightAction = true,
            onRightAction = { /*TODO*/ }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader(innerPadding = innerPadding)
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
            EmailField(
                text = loginViewModel.emailTextInput,
                onTextChange = loginViewModel::onEmailTextInputChange,
                isError = loginViewModel.isEmailError(),
                enableEdit = !loginViewModel.isProgressing
            )
            Spacer(modifier = Modifier.padding(vertical = innerPadding))
            PasswordField(
                text = loginViewModel.passwordTextInput,
                onTextChange = loginViewModel::onPasswordTextInputChange,
                enableEdit = !loginViewModel.isProgressing
            )
            AnimatedVisibility(visible = loginViewModel.failCause.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(innerPadding * 2),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = loginViewModel.failCause,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.error,
                    )
                }
            }
            AnimatedVisibility(visible = loginViewModel.failCause.isEmpty()) {
                Spacer(modifier = Modifier.padding(vertical = innerPadding * 2))
            }
            LoadingButton(
                text = stringResource(id = R.string.ui_loginScreen_loginButton),
                onClick = {
                    loginViewModel.onLogInRequest(localContext, onLoginDone)
                },
                enabled = loginViewModel.isButtonEnabled(),
                isLoading = loginViewModel.isProgressing,
                textFontSize = LoadingButtonConfig.TEXT_FONT_SIZE.sp,
                buttonWidth = LoadingButtonConfig.DEFAULT_WIDTH.dp,
                buttonHeight = LoadingButtonConfig.DEFAULT_HEIGHT.dp,
                loadingWidth = LoadingButtonConfig.PROGRESS_WIDTH.dp,
                transitionDuration = LoadingButtonConfig.STATE_TRANSITION_DURATION,
                textEnterTransition = fadeIn(
                    animationSpec = TweenSpec(delay = LoadingButtonConfig.TEXT_FADE_IN_DELAY)
                ),
            )
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
            AnimatedContent(
                targetState = loginViewModel.isNotConfirmed,
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(800)
                        ),
                        initialContentExit = fadeOut(
                            animationSpec = tween(400)
                        )
                    )
                }
            ) {
                AnimatedVisibility(
                    visible = loginViewModel.isNotConfirmed,
                    enter = fadeIn(animationSpec = tween(1500)),
                    exit = fadeOut(animationSpec = tween(0)),
                ) {
                    LoginFooter(
                        message = stringResource(id = R.string.ui_loginScreen_missingConfirmationCodeMessage),
                        actionText = stringResource(id = R.string.ui_loginScreen_confirmButton),
                        onAction = {
                            loginViewModel.onResendSignUpConfirmationRequest(localContext, onResendSignUpConfirmationDone)
                        },
                        actionEnable = !loginViewModel.isProgressing
                    )
                }
                AnimatedVisibility(
                    visible = !loginViewModel.isNotConfirmed,
                    enter = fadeIn(animationSpec = tween(1500)),
                    exit = fadeOut(animationSpec = tween(0)),
                ) {
                    LoginFooter(
                        message = stringResource(id = R.string.ui_loginScreen_footer),
                        actionText = stringResource(id = R.string.ui_loginScreen_signUpButton),
                        onAction = onNavigateToSignUp,
                        actionEnable = !loginViewModel.isProgressing
                    )
                }
            }
        }
    }
}

@Preview("Login Screen")
@Composable
fun PreviewLoginScreen() {
    val loginViewModel = LoginViewModel()
    loginViewModel.onEmailTextInputChange("example@email.com")
    loginViewModel.onPasswordTextInputChange("password")
    UrgentTheme {
        Surface {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginDone = {},
                onNavigateToSignUp = {},
                onResendSignUpConfirmationDone = {_, _ ->},
            )
        }
    }
}