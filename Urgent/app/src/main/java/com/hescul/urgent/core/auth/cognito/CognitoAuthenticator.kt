package com.hescul.urgent.core.auth.cognito

import android.content.Context
import androidx.navigation.NavHostController
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import timber.log.Timber
import java.lang.Exception

class CognitoAuthenticator(
    context: Context,
    cognitoIdentity: CognitoIdentity,
    clientConfig: ClientConfiguration = ClientConfiguration()
) {
    private val userPool = CognitoUserPool(
        context,
        cognitoIdentity.poolId,
        cognitoIdentity.clientId,
        cognitoIdentity.clientSecret,
        clientConfig,
        cognitoIdentity.region
    )

    companion object {
        private const val DEBUG_TAG = "authCognito"
        private const val DEFAULT_SIGNUP_FAIL_CAUSE = "Sign-up failed"
        private const val DEFAULT_CONFIRM_SIGNUP_FAIL_CAUSE = "Confirmation failed"
        private const val DEFAULT_SIGN_IN_FAIL_CAUSE = "Sign-in failed"
        private const val DEFAULT_RESEND_CONFIRMATION_FAIL_CAUSE = "Resend failed"
        private const val FAIL_CAUSE_DELIMITER = '.'
        private val FAIL_CAUSE_REPLACE_CHARS = listOf(':', '(')

        @JvmStatic
        fun reduceFailCause(cause: String): String {
            for (chr in FAIL_CAUSE_REPLACE_CHARS) {
                cause.replace(chr, FAIL_CAUSE_DELIMITER)
            }
            return cause.substringBefore(FAIL_CAUSE_DELIMITER).trimEnd()
        }
    }

    fun signUpUser(
        name: String,
        userId: String,
        password: String,
        onSignUpSuccess: (CognitoUser, SignUpResult) -> Unit,
        onSignUpFailure: (String) -> Unit,
        defaultFailCause: String = DEFAULT_SIGNUP_FAIL_CAUSE,
    ) {
        // create and set required attributes
        val userAttributes = CognitoUserAttributes()
        userAttributes.addAttribute("email", userId)
        userAttributes.addAttribute("name", name)

        // set up callback
        val signUpCallback = object: SignUpHandler {
            override fun onSuccess(user: CognitoUser?, result: SignUpResult?) {
                // Sign-up was successful
                Timber.tag(DEBUG_TAG).d("user has successfully signed up: user_id<${user?.userId}>")
                if (user == null || result == null) {
                    onSignUpFailure(defaultFailCause)
                }
                else {
                    onSignUpSuccess(user, result)
                }

            }

            override fun onFailure(exception: Exception?) {
                // Sign-up failed, check exception for the cause
                Timber.tag(DEBUG_TAG).e("sign-up failed: ${exception?.message}!")
                val failCause = exception?.message ?: defaultFailCause
                onSignUpFailure(failCause)
            }

        }

        // do sign up in background
        userPool.signUpInBackground(userId, password, userAttributes, null, signUpCallback)
    }

    fun confirmSignUp(
        userId: String,
        confirmationCode: String,
        forcedAliasCreation: Boolean,
        onConfirmSuccess: () -> Unit,
        onConfirmFailure: (String) -> Unit,
        defaultFailCause: String = DEFAULT_CONFIRM_SIGNUP_FAIL_CAUSE,
    ) {
        val cognitoUser = userPool.getUser(userId)
        val confirmCallback = object: GenericHandler {
            override fun onSuccess() {
                Timber.tag(DEBUG_TAG).d("confirm successfully!")
                onConfirmSuccess()
            }

            override fun onFailure(exception: Exception?) {
                Timber.tag(DEBUG_TAG).e("confirmation failed: ${exception?.message}")
                val failCause = exception?.message ?: defaultFailCause
                onConfirmFailure(failCause)
            }

        }
        cognitoUser.confirmSignUpInBackground(confirmationCode, forcedAliasCreation, confirmCallback)
    }

    /**
     * This method is asynchronous and performs network operations on a background thread.
     *
     * There are 2 scenarios:
     *  - (1) The tokens (Id, Access and Refresh) are cached on the device
     *  - (2) No valid tokens cached on the device
     *  - (3) All other error scenarios
     *
     *  For case (1), if the Id and Access tokens are present and they are valid, the [AuthenticationHandler.onSuccess]
     *  will be called with a [CognitoUserSession] that has reference to the valid token; otherwise,
     *  if the Id and Access tokens are expired, and if there is a valid refresh token, a network call is made
     *  to get new Id and Access tokens. If valid Id and Access tokens are retrieved, they are cached on the device
     *  and again, [AuthenticationHandler.onSuccess] will be called. The user is signed-in for this case.
     *
     *  For case (2), the callback method [AuthenticationHandler.getAuthenticationDetails] will be called where the
     *  [AuthenticationContinuation.authenticationDetails] will need to be supplied to continue the SignIn operation.
     *
     *  For case (3), [AuthenticationHandler.onFailure] will be called with the type and message of the exception
     *  and it is the responsibility of the caller to handle the exceptions appropriately.
     */
    fun loginUser(
        userId: String,
        onLoginSuccess: (CognitoUserSession) -> Unit,
        onLoginFailure: (String) -> Unit,
        onAuthenticationDetailsRequest: (AuthenticationContinuation?, String?) -> Unit,
        onMFACodeRequest: (MultiFactorAuthenticationContinuation) -> Unit,
        onAuthChallengeRequest: (ChallengeContinuation) -> Unit,
        defaultFailCause: String = DEFAULT_SIGN_IN_FAIL_CAUSE,
    ) {
        val cognitoUser = userPool.getUser(userId)  // get the user
        val authenticationHandler = object: AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                Timber.tag(DEBUG_TAG).d("logged in successfully! username<${userSession?.username}>")
                if (userSession == null) {
                    onLoginFailure(defaultFailCause)
                }
                else {
                    // Sign-in was successful, cognitoUserSession will contain tokens for the user
                    onLoginSuccess(userSession)
                }
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation?,
                userId: String?
            ) {
                Timber.tag(DEBUG_TAG).d("callback getAuthenticationDetails gets called")
                onAuthenticationDetailsRequest(authenticationContinuation, userId)
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                Timber.tag(DEBUG_TAG).d("callback getMFACode gets called")
                if (continuation == null) {
                    onLoginFailure(defaultFailCause)
                }
                else {
                    onMFACodeRequest(continuation)
                }
            }

            override fun onFailure(exception: Exception?) {
                Timber.tag(DEBUG_TAG).e("login failed: ${exception?.message}")
                val failCause = exception?.message ?: defaultFailCause
                onLoginFailure(failCause)
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                Timber.tag(DEBUG_TAG).d("callback authenticationChallenge gets called")
                if (continuation == null) {
                    onLoginFailure(defaultFailCause)
                }
                else {
                    onAuthChallengeRequest(continuation)
                }
            }
        }
        cognitoUser.getSessionInBackground(authenticationHandler)
    }

    fun resendSignUpConfirmation(
        userId: String,
        onResendConfirmationSuccess: (CognitoUserCodeDeliveryDetails) -> Unit,
        onResendConfirmationFailure: (String) -> Unit,
        defaultFailCause: String = DEFAULT_RESEND_CONFIRMATION_FAIL_CAUSE,
    ) {
        val cognitoUser = userPool.getUser(userId)
        val verificationHandler = object: VerificationHandler{
            override fun onSuccess(verificationCodeDeliveryMedium: CognitoUserCodeDeliveryDetails?) {
                if (verificationCodeDeliveryMedium == null) {
                    onResendConfirmationFailure(DEFAULT_RESEND_CONFIRMATION_FAIL_CAUSE)
                }
                else {
                    onResendConfirmationSuccess(verificationCodeDeliveryMedium)
                }
            }

            override fun onFailure(exception: Exception?) {
                val failCause = exception?.message ?: defaultFailCause
                onResendConfirmationFailure(failCause)
            }
        }
        cognitoUser.resendConfirmationCodeInBackground(verificationHandler)
    }
}