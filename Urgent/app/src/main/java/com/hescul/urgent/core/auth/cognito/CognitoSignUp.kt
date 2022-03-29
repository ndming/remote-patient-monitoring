package com.hescul.urgent.core.auth.cognito

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.amazonaws.util.VersionInfoUtils
import timber.log.Timber
import java.lang.Exception


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
    connectionTimeOut: Int = 15 * 1000,
    socketTimeOut: Int = 15 * 1000,
    maxConnections: Int = 10,
    userAgent: String = VersionInfoUtils.getUserAgent()
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
    poolId: String,
    clientId: String,
    clientSecret: String,
    clientRegion: Regions,
    clientConfig: ClientConfiguration = ClientConfiguration()
): CognitoUserPool {
    return CognitoUserPool(context, poolId, clientId, clientSecret, clientConfig, clientRegion)
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
    onSignUpSuccess: (CognitoUser, SignUpResult, CognitoUserAttributes) -> Unit,
    onSignUpFailure: (String) -> Unit,
    defaultFailCause: String = "Sign-up failed",
    timberDebugTag: String = "authCognito",
) {
    val signUpCallback = object: SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            // Sign-up was successful
            Timber.tag(timberDebugTag).d("user has successfully signed up!")
            if (user == null || signUpResult == null) {
                onSignUpFailure(defaultFailCause)
            }
            else {
                onSignUpSuccess(user, signUpResult, userAttributes)
            }

        }

        override fun onFailure(exception: Exception?) {
            // Sign-up failed, check exception for the cause
            Timber.tag(timberDebugTag).e("sign-up failed: ${exception?.message}!")
            val failCause = exception?.message ?: defaultFailCause
            onSignUpFailure(failCause)
        }

    }
    userPool.signUpInBackground(userId, password, userAttributes, null, signUpCallback)
}

