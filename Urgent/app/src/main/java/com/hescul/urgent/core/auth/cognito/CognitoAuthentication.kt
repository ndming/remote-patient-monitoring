package com.hescul.urgent.core.auth.cognito

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import timber.log.Timber
import java.lang.Exception



object ConfirmationCallback : GenericHandler {
    override fun onSuccess() {
        Timber.tag(CognitoConfig.DEBUG_TAG).d("user has successfully confirmed!")
    }

    override fun onFailure(exception: Exception?) {
        Timber.tag(CognitoConfig.DEBUG_TAG).e("confirmation failed: ${exception?.message}!")
    }

}

/**
 * Setup AWS service configuration. The default parameter are set to value determined from the
 * default settings from AWS SDK
 *
 * @param connectionTimeOut timeout for creating new connections
 * @param socketTimeOut timeout for reading from a connected socket
 * @param maxConnections max connection pool size
 * @param userAgent HTTP user agent header for AWS Java SDK clients
 */
fun createClientConfiguration(
    connectionTimeOut: Int = CognitoConfig.DEFAULT_CONNECTION_TIMEOUT,
    socketTimeOut: Int = CognitoConfig.DEFAULT_SOCKET_TIMEOUT,
    maxConnections: Int = CognitoConfig.DEFAULT_MAX_CONNECTIONS,
    userAgent: String = CognitoConfig.DEFAULT_USER_AGENT
): ClientConfiguration {
    val clientConfiguration = ClientConfiguration()
    clientConfiguration.connectionTimeout = connectionTimeOut
    clientConfiguration.socketTimeout = socketTimeOut
    clientConfiguration.maxConnections = maxConnections
    clientConfiguration.userAgent = userAgent
    return clientConfiguration
}

/**
 * Create a CognitoUserPool object to refer to your user pool.
 *
 * @param context the Android context in which this user pool is invoked
 * @param clientConfig a [ClientConfiguration] instance holding required client configurations
 *
 * @return a Cognito user pool ready to do the signing up authentication
 */
fun createCognitoUserPool(
    context: Context,
    clientConfig: ClientConfiguration = ClientConfiguration()
): CognitoUserPool {
    return CognitoUserPool(context,
        CognitoConfig.POOL_ID,
        CognitoConfig.CLIENT_ID,
        CognitoConfig.CLIENT_SECRET,
        clientConfig,
        CognitoConfig.REGION
    )
}

/**
 * Create a [CognitoUserAttributes] object and add user attributes
 *
 * @param email the client email address
 * @param name the client's provided name
 */
fun createCognitoAttribute(email: String, name: String): CognitoUserAttributes {
    val userAttributes = CognitoUserAttributes()
    userAttributes.addAttribute("email", email)
    userAttributes.addAttribute("name", name)
    return userAttributes
}

fun signUpUser(
    userPool: CognitoUserPool,
    userId: String,
    password: String,
    userAttributes: CognitoUserAttributes,
    onSignUpSuccess: () -> Unit = {},
    onSignUpFailure: (String?) -> Unit = {},
) {
    val signUpCallback = object: SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            // Sign-up was successful
            Timber.tag(CognitoConfig.DEBUG_TAG).d("user has successfully signed up!")
            onSignUpSuccess()
            // This user has to be confirmed and a confirmation code was sent to the user
            // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
            // Get the confirmation code from user
            if (signUpResult?.isUserConfirmed != true) {
                Timber.tag(CognitoConfig.DEBUG_TAG).d("user has to be confirmed!")
                Timber.tag(CognitoConfig.DEBUG_TAG).d("confirmation code method: ${signUpResult?.codeDeliveryDetails?.destination}")
            }
            else {
                // The user has already been confirmed
                Timber.tag(CognitoConfig.DEBUG_TAG).d("user has already been confirmed!")
            }
        }

        override fun onFailure(exception: Exception?) {
            // Sign-up failed, check exception for the cause
            Timber.tag(CognitoConfig.DEBUG_TAG).e("sign-up failed: ${exception?.message}!")
            onSignUpFailure(exception?.message)
        }

    }
    userPool.signUpInBackground(userId, password, userAttributes, null, signUpCallback)
}

