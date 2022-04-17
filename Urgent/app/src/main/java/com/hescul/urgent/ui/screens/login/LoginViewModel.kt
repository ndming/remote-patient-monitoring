package com.hescul.urgent.ui.screens.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.hescul.urgent.R
import com.hescul.urgent.core.cognito.CognitoAuthenticator
import com.hescul.urgent.core.utils.InfoValidator

class LoginViewModel : ViewModel() {
    companion object {
        private const val USER_NOT_CONFIRMED_MESSAGE = "User is not confirmed"
    }

    // ui states
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

    // ui callbacks
    fun onEmailTextInputChange(text: String) {
        emailTextInput = text
    }
    fun onPasswordTextInputChange(text: String) {
        passwordTextInput = text
    }

    // utilities
    fun isEmailError(): Boolean {
        return emailTextInput.isNotEmpty() && !InfoValidator.isEmailValid(emailTextInput)
    }
    fun isButtonEnabled() = InfoValidator.isEmailValid(emailTextInput)
            && passwordTextInput.isNotEmpty()
            && !isProgressing

    // functional callbacks
    private fun onLogInProgress() {
        failCause = ""
        isProgressing = true
    }
    private fun onLogInSuccess() {
        isProgressing = false
    }
    private fun onLogInFailure(cause: String) {
        isProgressing = false
        isNotConfirmed = cause == USER_NOT_CONFIRMED_MESSAGE
        failCause = cause
    }
    fun onLogInRequest(context: Context, onLogInDone: (CognitoUserSession) -> Unit) {
        onLogInProgress()
        CognitoAuthenticator.requestLogIn(
            context = context,
            userId = emailTextInput,
            onLoginSuccess = { userSession ->
                onLogInSuccess()
                onLogInDone(userSession)
            },
            onLoginFailure = { cause ->
                onLogInFailure(CognitoAuthenticator.reduceFailCause(cause))
            },
            onAuthenticationDetailsRequest = { continuation ->
                val authenticationDetails = AuthenticationDetails(emailTextInput,passwordTextInput,null)
                continuation.setAuthenticationDetails(authenticationDetails)
                continuation.continueTask()
            },
            onMFACodeRequest = {},  // MFA is not being used for now
            onAuthChallengeRequest = {}, // challenge is not being handled for now
            defaultFailCause = context.getString(R.string.ui_loginScreen_defaultFailCause)
        )
    }
    fun onResendSignUpConfirmationRequest(context: Context, onDone: (String, CognitoUserCodeDeliveryDetails) -> Unit) {
        onLogInProgress()
        CognitoAuthenticator.requestResendSignUpConfirmation(
            context = context,
            userId = emailTextInput,
            onResendConfirmationSuccess = { codeDeliveryDetails ->
                onLogInSuccess()
                onDone(emailTextInput, codeDeliveryDetails)
            },
            onResendConfirmationFailure = { cause ->
                onLogInFailure(CognitoAuthenticator.reduceFailCause(cause))
            },
            defaultFailCause = context.getString(R.string.ui_loginScreen_resendSignUpConfirmationDefaultFailCause)
        )
    }

    /**
     * Reset the state of the [LoginScreen], should be called before navigating to this screen
     */
    fun reset() {
        isNotConfirmed = false
        failCause = ""
        isProgressing = false
        passwordTextInput = ""
    }
}