package com.hescul.urgent.navigation

import android.content.Context
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.hescul.urgent.R
import com.hescul.urgent.core.auth.cognito.*
import com.hescul.urgent.core.utils.MessageProcessor
import com.hescul.urgent.model.UrgentViewModel
import com.hescul.urgent.ui.screens.signup.SignUpScreen

fun NavGraphBuilder.signUpComposable(
    navController: NavHostController,
    urgentViewModel: UrgentViewModel,
    modifier: Modifier = Modifier
) {
    composable(UrgentScreen.SignUp.name) {
        val defaultFailCause = stringResource(id = R.string.ui_signUpScreen_defaultFailCause)
        val localContext = LocalContext.current
        SignUpScreen(
            signUpViewModel = urgentViewModel.getSignUpViewModel(),
            modifier = modifier,
            onSignUpRequest = {
                urgentViewModel.getSignUpViewModel().onSignUpProgress()
                onSignUpRequest(localContext, navController, urgentViewModel, defaultFailCause)
            }
        )
    }
}

private fun onSignUpRequest(context: Context, navController: NavHostController, urgentViewModel: UrgentViewModel, defaultFailCause: String) {
    val clientAttribute = createCognitoAttribute(
        email = urgentViewModel.getSignUpViewModel().emailTextInput,
        name = urgentViewModel.getSignUpViewModel().userNameTextInput,
    )
    val clientConfig = createClientConfiguration(
        connectionTimeOut = CognitoConfig.CONNECTION_TIMEOUT,
        socketTimeOut = CognitoConfig.SOCKET_TIMEOUT,
        maxConnections = CognitoConfig.MAX_CONNECTIONS,
        userAgent = CognitoConfig.USER_AGENT
    )
    val userPool = createCognitoUserPool(
        context = context,
        poolId = CognitoConfig.POOL_ID,
        clientId = CognitoConfig.CLIENT_ID,
        clientSecret = CognitoConfig.CLIENT_SECRET,
        clientRegion = CognitoConfig.REGION,
        clientConfig = clientConfig
    )
    signUpUser(
        userPool = userPool,
        userId = urgentViewModel.getSignUpViewModel().emailTextInput,
        password = urgentViewModel.getSignUpViewModel().passwordTextInput,
        userAttributes = clientAttribute,
        onSignUpSuccess = { cognitoUser, signUpResult, userAttributes ->
            onSignUpDone(
                navController = navController,
                urgentViewModel = urgentViewModel,
                cognitoUser = cognitoUser,
                signUpResult = signUpResult,
                userAttributes = userAttributes
            )
        },
        onSignUpFailure = { cause ->
            urgentViewModel.getSignUpViewModel()
                .onSignUpFailure(MessageProcessor.processFailCause(cause))
        },
        defaultFailCause = defaultFailCause,
        timberDebugTag = CognitoConfig.DEBUG_TAG
    )
}

private fun onSignUpDone(
    navController: NavHostController,
    urgentViewModel: UrgentViewModel,
    cognitoUser: CognitoUser,
    signUpResult: SignUpResult,
    userAttributes: CognitoUserAttributes
) {
    urgentViewModel.cognitoUser = cognitoUser
    urgentViewModel.signUpResult = signUpResult
    urgentViewModel.userAttributes = userAttributes
    if (!signUpResult.isUserConfirmed) {
        urgentViewModel.getConfirmViewModel().reset()
        navController.navigate(UrgentScreen.Confirm.name)
    }
    else {
        // TODO: navigate to login screen
    }
}