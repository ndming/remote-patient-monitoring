package com.hescul.urgent.ui.screens.confirm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.R
import com.hescul.urgent.core.auth.cognito.CognitoAuthenticator
import com.hescul.urgent.core.utils.InfoValidator

class ConfirmViewModel : ViewModel() {
    companion object {
        private const val UNINITIALIZED_ERROR = "An error occurred. Please try again"
        private const val SAMPLE_USER_ID = "example@mail.com"
        private const val SAMPLE_MEDIUM = "EMAIL"
        private const val SAMPLE_DESTINATION = "e***@m***"
    }

    // late init data
    var userId = SAMPLE_USER_ID
        private set
    var medium = SAMPLE_MEDIUM
        private set
    var destination = SAMPLE_DESTINATION
        private set

    // ui states
    var confirmCodeTextInput by mutableStateOf("")
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var isConfirmSucceeded by mutableStateOf(false)
        private set
    var failCause by mutableStateOf("")
        private set
    fun onConfirmTextInputChange(text: String) {
        confirmCodeTextInput = text
    }

    // functional callbacks
    private fun onConfirmProgress() {
        failCause = ""
        isProgressing = true
    }
    private fun onConfirmSuccess() {
        isConfirmSucceeded = true
        isProgressing = false
    }
    private fun onConfirmFailure(cause: String) {
        isProgressing = false
        failCause = cause
    }

    // functional
    fun onConfirmSignUpRequest(context: Context) {
        if (userId == SAMPLE_USER_ID) {
            onConfirmFailure(UNINITIALIZED_ERROR)
        }
        else {
            onConfirmProgress()
            CognitoAuthenticator.requestConfirmSignUp(
                context = context,
                userId = userId,
                confirmationCode = confirmCodeTextInput,
                forcedAliasCreation = false,
                onConfirmSuccess = {
                    onConfirmSuccess()
                },
                onConfirmFailure = { cause ->
                    onConfirmFailure(CognitoAuthenticator.reduceFailCause(cause))
                },
                defaultFailCause = context.getString(R.string.ui_confirmScreen_defaultFailCause)
            )
        }
    }

    // utilities
    fun isConfirmCodeError(): Boolean = confirmCodeTextInput.isNotEmpty()
            && !InfoValidator.isConfirmCodeValid(confirmCodeTextInput)

    /**
     * Reset the state of the [ConfirmScreen], should be called before navigating to this screen
     */
    fun reset() {
        confirmCodeTextInput = ""
        isProgressing = false
        isConfirmSucceeded = false
        failCause = ""
    }

    // must be called
    fun updateConfirmationIdentity(userId: String, medium: String, destination: String) {
        this.userId = userId
        this.medium = medium
        this.destination = destination
    }
}