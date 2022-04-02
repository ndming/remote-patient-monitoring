package com.hescul.urgent.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.core.utils.InfoValidator

class LoginViewModel : ViewModel() {
    private val userIsNotConfirmedMessage = "User is not confirmed"

    var emailTextInput by mutableStateOf("")
        private set
    var passwordTextInput by mutableStateOf("")
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var failCause by mutableStateOf("")
        private set
    var isNotConfirmed by mutableStateOf(false)
        private set

    fun onEmailTextInputChange(text: String) {
        emailTextInput = text
    }
    fun onPasswordTextInputChange(text: String) {
        passwordTextInput = text
    }

    fun isEmailError(): Boolean {
        return emailTextInput.isNotEmpty() && !InfoValidator.isEmailValid(emailTextInput)
    }
    fun isButtonEnabled() = InfoValidator.isEmailValid(emailTextInput)
            && passwordTextInput.isNotEmpty()
            && !isProgressing

    fun onLoginProgress() {
        failCause = ""
        isNotConfirmed = false
        isProgressing = true
    }
    fun onLoginFailure(cause: String) {
        isProgressing = false
        if (cause == userIsNotConfirmedMessage) {
            isNotConfirmed = true
        }
        failCause = cause
    }
    fun reset() {
        isNotConfirmed = false
        failCause = ""
        isProgressing = false
        passwordTextInput = ""
    }
}