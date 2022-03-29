package com.hescul.urgent.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.hescul.urgent.R
import com.hescul.urgent.core.auth.cognito.CognitoConfig
import com.hescul.urgent.core.auth.cognito.confirmUser
import com.hescul.urgent.core.utils.MessageProcessor
import com.hescul.urgent.model.UrgentViewModel
import com.hescul.urgent.ui.screens.confirm.ConfirmScreen

fun NavGraphBuilder.confirmComposable(
    navController: NavHostController,
    urgentViewModel: UrgentViewModel,
    modifier: Modifier = Modifier
) {
    composable(UrgentScreen.Confirm.name) {
        val defaultUserName = stringResource(id = R.string.ui_confirmScreen_defaultUserName)
        val defaultFailCause = stringResource(id = R.string.ui_confirmScreen_defaultFailCause)
        ConfirmScreen(
            confirmViewModel = urgentViewModel.getConfirmViewModel(),
            modifier = modifier,
            userName = urgentViewModel.userAttributes.attributes["name"] ?: defaultUserName,
            confirmMedium = urgentViewModel.signUpResult.codeDeliveryDetails.deliveryMedium,
            confirmDestination = urgentViewModel.signUpResult.codeDeliveryDetails.destination,
            onConfirmRequest = {
                // This will cause confirmation to fail if the user attribute has been verified for another user in the same pool
                val forcedAliasCreation = false
                confirmUser(
                    user = urgentViewModel.cognitoUser,
                    confirmationCode = urgentViewModel.getConfirmViewModel().confirmCodeTextInput,
                    forcedAliasCreation = forcedAliasCreation,
                    onConfirmSuccess = urgentViewModel.getConfirmViewModel()::onConfirmSuccess,
                    onConfirmFailure = { cause ->
                        urgentViewModel.getConfirmViewModel()
                            .onConfirmFailure(MessageProcessor.processFailCause(cause))
                    },
                    defaultFailCause = defaultFailCause,
                    timberDebugTag = CognitoConfig.DEBUG_TAG
                )
            },
            onBackPressed = {
                urgentViewModel.getSignUpViewModel().reset()
                navController.popBackStack()
            },
            onBackToLogInPressed = {
                // TODO: back to LOGIN page
            }
        )
    }
}