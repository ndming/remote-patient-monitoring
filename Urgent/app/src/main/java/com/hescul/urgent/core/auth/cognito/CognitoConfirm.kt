package com.hescul.urgent.core.auth.cognito

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import timber.log.Timber
import java.lang.Exception

fun confirmUser(
    user: CognitoUser,
    confirmationCode: String,
    forcedAliasCreation: Boolean,
    onConfirmSuccess: () -> Unit,
    onConfirmFailure: (String) -> Unit,
    defaultFailCause: String = "Confirmation failed",
    timberDebugTag: String = "authCognito",
) {
    val confirmCallback = object: GenericHandler {
        override fun onSuccess() {
            Timber.tag(timberDebugTag).d("confirm successfully!")
            onConfirmSuccess()
        }

        override fun onFailure(exception: Exception?) {
            Timber.tag(timberDebugTag).e("confirmation failed: ${exception?.message}")
            val failCause = exception?.message ?: defaultFailCause
            onConfirmFailure(failCause)
        }

    }
    user.confirmSignUpInBackground(confirmationCode, forcedAliasCreation, confirmCallback)
}