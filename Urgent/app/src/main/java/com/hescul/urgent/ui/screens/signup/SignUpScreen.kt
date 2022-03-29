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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
    sidePadding: Dp = 40.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = sidePadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignUpHeader()
        SignUpInfoField(
            signUpViewModel = signUpViewModel,
            modifier = Modifier,
            enableEdit = !signUpViewModel.isProgressing
        )
        AnimatedVisibility(visible = signUpViewModel.failCause.isNotEmpty()) {
            if (signUpViewModel.failCause.isNotEmpty()) {
                Text(
                    text = signUpViewModel.failCause,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 40.dp))
        SignUpButton(
            onSignUp = signUpViewModel::onSignUpRequest,
            buttonEnable = signUpViewModel.isButtonEnable(),
            isProgressing = signUpViewModel.isProgressing
        )
        SignUpFooter()
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
        modifier = modifier
    ) {
        UserNameField(
            text = signUpViewModel.userNameTextInput,
            onTextChange = signUpViewModel::onNameTextInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = innerPadding),
            enableEdit = enableEdit
        )
        EmailField(
            text = signUpViewModel.emailTextInput,
            onTextChange = signUpViewModel::onEmailTextInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = innerPadding),
            isError = signUpViewModel.isEmailError(),
            enableEdit = enableEdit
        )
        PasswordField(
            text = signUpViewModel.passwordTextInput,
            onTextChange = signUpViewModel::onPasswordTextInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = innerPadding),
            isError = signUpViewModel.isPasswordError(),
            enableEdit = enableEdit
        )
        ConfirmPasswordField(
            text = signUpViewModel.confirmPasswordTextInput,
            onTextChange = signUpViewModel::onConfirmPasswordTextInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = innerPadding),
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
            SignUpScreen(signUpViewModel = signUpViewModel)
        }
    }
}