package com.hescul.urgent.ui.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.core.utils.InfoValidator


class SignUpViewModel : ViewModel() {
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

    fun onSignUpProgress() {
        failCause = ""
        isProgressing = true
    }

    fun reset() {
        isProgressing = false
        passwordTextInput = ""
        confirmPasswordTextInput = ""
        failCause = ""
    }

    fun onSignUpFailure(cause: String) {
        isProgressing = false
        failCause = cause
    }
}