package com.hescul.urgent.ui.screens.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R
import com.hescul.urgent.ui.screens.signup.EmailField
import com.hescul.urgent.ui.screens.signup.PasswordField
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.LoadingButton
import com.hescul.urgent.ui.versatile.config.LoadingButtonConfig
import com.hescul.urgent.ui.versatile.config.ViewConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 5.dp,
    contentPadding: Dp = 40.dp,
    onLoginRequest: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onReconfirmRequest: () -> Unit,
) {
    Column(
        modifier = modifier
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
                loginViewModel.onLoginProgress()
                onLoginRequest()
            },
            enabled = loginViewModel.isButtonEnabled(),
            isLoading = loginViewModel.isProgressing,
            textFontSize = LoadingButtonConfig.TEXT_FONT_SIZE.sp,
            buttonWidth = ViewConfig.TEXT_FIELD_DEFAULT_WIDTH.dp,
            buttonHeight = ViewConfig.TEXT_FIELD_DEFAULT_HEIGHT.dp,
            loadingWidth = ViewConfig.CIRCULAR_PROGRESS_INDICATOR_DEFAULT_WIDTH.dp,
            transitionDuration = LoadingButtonConfig.STATE_TRANSITION_DURATION,
            textEnterTransition = fadeIn(
                animationSpec = TweenSpec(delay = LoadingButtonConfig.TEXT_FADE_IN_DELAY)
            ),
        )
        Spacer(modifier = Modifier.padding(vertical = contentPadding))
        AnimatedContent(
            targetState = loginViewModel.isNotConfirmed,
            contentAlignment = Alignment.Center
        ) {
            if (loginViewModel.isNotConfirmed) {
                LoginFooter(
                    message = stringResource(id = R.string.ui_loginScreen_missingConfirmationCodeMessage),
                    actionText = stringResource(id = R.string.ui_loginScreen_confirmButton),
                    onAction = onReconfirmRequest,
                    actionEnable = !loginViewModel.isProgressing
                )
            }
            else {
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

@Preview("Login Screen")
@Composable
fun PreviewLoginScreen() {
    val loginViewModel = LoginViewModel()
    loginViewModel.onEmailTextInputChange("example@email.com")
    loginViewModel.onPasswordTextInputChange("password")
    loginViewModel.onLoginFailure("User is not confirmed")
    UrgentTheme {
        Surface {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginRequest = {},
                onNavigateToSignUp = {},
                onReconfirmRequest = {},
            )
        }
    }
}