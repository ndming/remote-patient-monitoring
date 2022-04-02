package com.hescul.urgent

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.amazonaws.ClientConfiguration
import com.hescul.urgent.core.auth.cognito.CognitoAuthenticator
import com.hescul.urgent.core.auth.cognito.CognitoConfig
import com.hescul.urgent.core.auth.cognito.CognitoIdentity
import com.hescul.urgent.navigation.UrgentNavHost
import com.hescul.urgent.navigation.UrgentScreen
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.login.LoginViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vms = initViewModels()
        val cognito = initCognito(applicationContext)
        setContent {
            UrgentNavHost(
                startDestination = UrgentScreen.Login.name,
                cognito = cognito,
                viewModels = vms,
            )
        }

    }

    private fun initViewModels(): UrgentViewModels {
        val signUpViewModel by viewModels<SignUpViewModel>()
        val confirmViewModel by viewModels<ConfirmViewModel>()
        val loginViewModel by viewModels<LoginViewModel>()
        return UrgentViewModels(
            loginViewModel = loginViewModel,
            signUpViewModel = signUpViewModel,
            confirmViewModel = confirmViewModel
        )
    }

    private fun initCognito(context: Context): CognitoAuthenticator {
        val cognitoIdentity = CognitoIdentity(
            poolId = CognitoConfig.POOL_ID,
            clientId = CognitoConfig.CLIENT_ID,
            clientSecret = CognitoConfig.CLIENT_SECRET,
            region = CognitoConfig.REGION
        )
        val cognitoConfig = ClientConfiguration()
        cognitoConfig.connectionTimeout = CognitoConfig.CONNECTION_TIMEOUT
        cognitoConfig.socketTimeout = CognitoConfig.SOCKET_TIMEOUT
        cognitoConfig.maxConnections = CognitoConfig.MAX_CONNECTIONS
        cognitoConfig.userAgent = CognitoConfig.USER_AGENT
        return CognitoAuthenticator(context, cognitoIdentity, cognitoConfig)
    }
}

