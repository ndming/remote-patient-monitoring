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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R
import com.hescul.urgent.core.utils.InfoValidator
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.config.LoadingButtonConfig
import com.hescul.urgent.ui.versatile.config.ViewConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConfirmScreen(
    confirmViewModel: ConfirmViewModel,
    confirmMedium: String,
    confirmDestination: String,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 5.dp,
    onBackToLogInPressed: () -> Unit = {},
) {
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
            confirmMedium = confirmMedium,
            confirmDestination = confirmDestination,
            enableEdit = !confirmViewModel.isProgressing,
            onConfirmRequest = {
                if (!confirmViewModel.isConfirmSucceeded) {
                    confirmViewModel.onConfirmProgress()
                    onConfirmRequest()
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
                    confirmViewModel.onConfirmProgress()
                    onConfirmRequest()
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
                onClick = onBackToLogInPressed,
                modifier = Modifier
                    .width(ViewConfig.TEXT_FIELD_DEFAULT_WIDTH.dp)
                    .height(ViewConfig.TEXT_FIELD_DEFAULT_HEIGHT.dp),
                enabled = !confirmViewModel.isProgressing,
            ) {
                Text(
                    text = stringResource(id = R.string.ui_confirmScreen_backToLoginButton),
                    textAlign = TextAlign.Center,
                    fontSize = LoadingButtonConfig.TEXT_FONT_SIZE.sp
                )
            }
        }
    }

}



@Preview(
    name = "Sign Up Screen",
    widthDp = 720,
    heightDp = 1280,
)
@Composable
fun PreviewConfirmScreen() {
    val confirmViewModel = ConfirmViewModel()
    UrgentTheme {
        Surface {
            ConfirmScreen(
                confirmViewModel = confirmViewModel,
                confirmMedium = "EMAIL",
                confirmDestination = "d**1@gmail.com",
                onConfirmRequest = {}
            )
        }
    }
}