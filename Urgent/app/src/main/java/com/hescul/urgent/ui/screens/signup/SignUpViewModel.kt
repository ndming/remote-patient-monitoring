package com.hescul.urgent.ui.screens.signup

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.hescul.urgent.R
import com.hescul.urgent.core.auth.cognito.CognitoAuthenticator
import com.hescul.urgent.core.utils.InfoValidator


class SignUpViewModel : ViewModel() {
    // ui states
    var nameTextInput by mutableStateOf("")
        private set
    var emailTextInput by mutableStateOf("")
        private set
    var passwordTextInput by mutableStateOf("")
        private set
    var confirmPasswordTextInput by mutableStateOf("")
        private set

    // functional states
    var isProgressing by mutableStateOf(false)
        private set
    var failCause by mutableStateOf("")
        private set

    // ui callbacks
    fun onNameTextInputChange(text: String) {
        nameTextInput = text
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

    // utilities
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


    // functional callbacks
    private fun onSignUpProgress() {
        failCause = ""
        isProgressing = true
    }
    private fun onSignUpSuccess() {
        isProgressing = false
    }
    private fun onSignUpFailure(cause: String) {
        isProgressing = false
        failCause = cause
    }
    fun onSignUpRequest(
        context: Context,
        onSignUpDone: (CognitoUser, SignUpResult) -> Unit,
    ) {
        // change state to progress
        onSignUpProgress()

        // create a user attribute
        val userAttributes = CognitoUserAttributes()
        userAttributes.addAttribute("email", emailTextInput)
        userAttributes.addAttribute("name", nameTextInput)

        // request sign-up
        CognitoAuthenticator.requestSignUp(
            context = context,
            userId = emailTextInput,
            password = passwordTextInput,
            userAttributes = userAttributes,
            onSignUpSuccess = { cognitoUser, signUpResult ->
                onSignUpSuccess()
                onSignUpDone(cognitoUser, signUpResult)
            },
            onSignUpFailure = { cause ->
                onSignUpFailure(CognitoAuthenticator.reduceFailCause(cause))
            },
            defaultFailCause = context.getString(R.string.ui_signUpScreen_defaultFailCause)
        )
    }


    /**
     * Reset the state of the [SignUpScreen], should be called before navigating to this screen
     */
    fun reset() {
        isProgressing = false
        passwordTextInput = ""
        confirmPasswordTextInput = ""
        failCause = ""
    }
}