package com.hescul.urgent.ui.screens.opening


import android.content.Context
import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.hescul.urgent.core.cognito.CognitoAuthenticator

class OpeningViewModel : ViewModel() {

    fun onLaunch(context: Context, onAutoLoginSuccess: (CognitoUserSession) -> Unit, onAutoLoginFailure: (String) -> Unit) {
        val userId = CognitoAuthenticator.getCurrentUserId(context)
        if (userId == null) {
            onAutoLoginFailure("")
        }
        else {
            CognitoAuthenticator.requestLogIn(
                context = context,
                userId = userId,
                onLoginSuccess = onAutoLoginSuccess,
                onLoginFailure = { onAutoLoginFailure(userId) },
                onAuthenticationDetailsRequest = { onAutoLoginFailure(userId) },
                onAuthChallengeRequest = { onAutoLoginFailure(userId) },
                onMFACodeRequest = { onAutoLoginFailure(userId) }
            )
        }
    }


}