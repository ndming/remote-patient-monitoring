package com.hescul.urgent.ui.screens.confirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.core.utils.InfoValidator
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.config.InfoTextFieldConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConfirmScreen(
    confirmViewModel: ConfirmViewModel,
    onBackToLogIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current
    val innerPadding = 5.dp
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ConfirmCodeField(
            text = confirmViewModel.confirmCodeTextInput,
            onTextChange = confirmViewModel::onConfirmTextInputChange,
            confirmMedium = confirmViewModel.medium,
            confirmDestination = confirmViewModel.destination,
            enableEdit = !confirmViewModel.isProgressing,
            onConfirmRequest = {
                if (!confirmViewModel.isConfirmSucceeded) {
                    confirmViewModel.onConfirmSignUpRequest(localContext)
                }
            },
            isError = confirmViewModel.isConfirmCodeError()
        )
        AnimatedVisibility(visible = confirmViewModel.failCause.isNotEmpty()) {
            Text(
                text = confirmViewModel.failCause,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(innerPadding * 2)
            )
        }
        AnimatedVisibility(visible = confirmViewModel.failCause.isEmpty()) {
            Spacer(modifier = Modifier.padding(vertical = innerPadding))
        }
        ConfirmButton(
            onConfirmRequest = {
                if (!confirmViewModel.isConfirmSucceeded) {
                    confirmViewModel.onConfirmSignUpRequest(localContext)
                }
            },
            isProgressing = confirmViewModel.isProgressing,
            buttonEnable = !confirmViewModel.isProgressing
                    && InfoValidator.isConfirmCodeValid(confirmViewModel.confirmCodeTextInput),
            isConfirmSucceeded = confirmViewModel.isConfirmSucceeded
        )
        Spacer(modifier = Modifier.padding(vertical = innerPadding))
        AnimatedVisibility(visible = confirmViewModel.isConfirmSucceeded) {
            OutlinedButton(
                onClick = onBackToLogIn,
                modifier = Modifier
                    .width(InfoTextFieldConfig.WIDTH.dp)
                    .height(InfoTextFieldConfig.HEIGHT.dp),
                enabled = !confirmViewModel.isProgressing,
            ) {
                Text(
                    text = stringResource(id = R.string.ui_confirmScreen_backToLoginButton),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}



@Preview(
    name = "Confirm Screen",
)
@Composable
fun PreviewConfirmScreen() {
    val confirmViewModel = ConfirmViewModel()
    confirmViewModel.updateConfirmationIdentity(
        userId = "",
        medium = "EMAIL",
        destination = "d***@m***"
    )
    UrgentTheme {
        Surface {
            ConfirmScreen(
                confirmViewModel = confirmViewModel,
                onBackToLogIn = {},
            )
        }
    }
}