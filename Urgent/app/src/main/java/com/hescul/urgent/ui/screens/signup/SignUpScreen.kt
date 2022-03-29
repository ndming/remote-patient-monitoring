package com.hescul.urgent.ui.screens.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 5.dp,
    onSignUpRequest: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignUpHeader(headerInnerPadding = innerPadding)
        SignUpInfoField(
            signUpViewModel = signUpViewModel,
            modifier = Modifier,
            enableEdit = !signUpViewModel.isProgressing,
            innerPadding = innerPadding
        )
        AnimatedVisibility(visible = signUpViewModel.failCause.isNotEmpty()) {
            Text(
                text = signUpViewModel.failCause,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(innerPadding * 2)
            )
        }
        AnimatedVisibility(visible = signUpViewModel.failCause.isEmpty()) {
            Spacer(modifier = Modifier.padding(vertical = innerPadding * 2))
        }
        SignUpButton(
            onSignUp = onSignUpRequest,
            buttonEnable = signUpViewModel.isButtonEnable(),
            isProgressing = signUpViewModel.isProgressing
        )
        SignUpFooter(footerPadding = innerPadding * 2)
    }

}

@Composable
fun SignUpInfoField(
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 5.dp,
    enableEdit: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserNameField(
            text = signUpViewModel.userNameTextInput,
            onTextChange = signUpViewModel::onNameTextInputChange,
            enableEdit = enableEdit
        )
        Spacer(modifier = Modifier.padding(vertical = innerPadding))
        EmailField(
            text = signUpViewModel.emailTextInput,
            onTextChange = signUpViewModel::onEmailTextInputChange,
            isError = signUpViewModel.isEmailError(),
            enableEdit = enableEdit
        )
        Spacer(modifier = Modifier.padding(vertical = innerPadding))
        PasswordField(
            text = signUpViewModel.passwordTextInput,
            onTextChange = signUpViewModel::onPasswordTextInputChange,
            isError = signUpViewModel.isPasswordError(),
            enableEdit = enableEdit
        )
        Spacer(modifier = Modifier.padding(vertical = innerPadding))
        ConfirmPasswordField(
            text = signUpViewModel.confirmPasswordTextInput,
            onTextChange = signUpViewModel::onConfirmPasswordTextInputChange,
            isError = signUpViewModel.isConfirmPasswordError(),
            enableEdit = enableEdit
        )
    }
}


@Preview(
    name = "Sign Up Screen",
    widthDp = 720,
    heightDp = 1280
)
@Composable
fun PreviewSignUpScreen() {
    val signUpViewModel = SignUpViewModel()
    signUpViewModel.onEmailTextInputChange("example@gmail.com")
    signUpViewModel.onPasswordTextInputChange("password")
    signUpViewModel.onConfirmPasswordTextInputChange("password")
    UrgentTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            SignUpScreen(
                signUpViewModel = signUpViewModel,
                onSignUpRequest = {}
            )
        }
    }
}