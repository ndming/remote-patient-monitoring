package com.hescul.urgent.ui.screens.signup

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.hescul.urgent.R
import com.hescul.urgent.core.auth.cognito.createClientConfiguration
import com.hescul.urgent.core.auth.cognito.createCognitoAttribute
import com.hescul.urgent.core.auth.cognito.createCognitoUserPool
import com.hescul.urgent.core.auth.cognito.signUpUser
import com.hescul.urgent.core.utils.InfoValidator


class SignUpViewModel() : ViewModel() {
    var userNameTextInput by mutableStateOf("")
        private set
    var emailTextInput by mutableStateOf("")
        private set
    var passwordTextInput by mutableStateOf("")
        private set
    var confirmPasswordTextInput by mutableStateOf("")
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var failCause by mutableStateOf("")
        private set

    fun onNameTextInputChange(text: String) {
        userNameTextInput = text
    }
    fun onEmailTextInputChange(text: String) {
        emailTextInput = text
    }
    fun onPasswordTextInputChange(text: String) {
        passwordTextInput = text
    }
    fun onConfirmPasswordTextInputChange(text: String) {
        confirmPasswordTextInput = text
    }

    fun isEmailError(): Boolean {
        return emailTextInput.isNotEmpty() && !InfoValidator.isEmailValid(emailTextInput)
    }
    fun isPasswordError(): Boolean {
        return passwordTextInput.isNotEmpty() && !InfoValidator.isPasswordValid(passwordTextInput)
    }
    fun isConfirmPasswordError(): Boolean {
        return confirmPasswordTextInput.isNotEmpty() && confirmPasswordTextInput != passwordTextInput
    }
    fun isButtonEnable() = InfoValidator.isEmailValid(emailTextInput)
            && InfoValidator.isPasswordValid(passwordTextInput)
            && confirmPasswordTextInput == passwordTextInput
            && !isProgressing

    fun onSignUpRequest(localContext: Context) {
        failCause = ""
        isProgressing = true
        val clientAttribute = createCognitoAttribute(
            email = emailTextInput,
            name = userNameTextInput,
        )
        val userPool = createCognitoUserPool(localContext, createClientConfiguration())
        signUpUser(
            userPool = userPool,
            userId = emailTextInput,
            password = passwordTextInput,
            userAttributes = clientAttribute,
            onSignUpSuccess = this::onSignUpSuccess,
            onSignUpFailure = this::onSignUpFailure
        )
    }

    private fun onSignUpSuccess() {
        isProgressing = false
    }
    private fun onSignUpFailure(cause: String?) {
        isProgressing = false
        failCause = cause?.substringBefore('(') ?: Resources.getSystem().getString(R.string.ui_signUpScreen_unknownFailCause)
    }
}