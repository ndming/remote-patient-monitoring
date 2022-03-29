package com.hescul.urgent.ui.screens.confirm

import androidx.activity.compose.BackHandler
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
    userName: String,
    confirmMedium: String,
    confirmDestination: String,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier,
    headerPadding: Dp = 30.dp,
    contentPadding: Dp = 5.dp,
    onBackPressed: () -> Unit = {},
    onBackToLogInPressed: () -> Unit = {},
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.padding(vertical = headerPadding))
        Text(
            text = "${stringResource(id = R.string.ui_confirmScreen_greeting)}\n$userName!",
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = contentPadding * 2)
        )
        Spacer(modifier = Modifier.padding(vertical = headerPadding))
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
                modifier = Modifier.padding(contentPadding * 2)
            )
        }
        AnimatedVisibility(visible = confirmViewModel.failCause.isEmpty()) {
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
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
        Spacer(modifier = Modifier.padding(vertical = contentPadding))
        OutlinedButton(
            onClick = onBackToLogInPressed,
            modifier = Modifier
                .width(ViewConfig.TEXT_FIELD_DEFAULT_WIDTH.dp)
                .height(ViewConfig.TEXT_FIELD_DEFAULT_HEIGHT.dp),
            enabled = !confirmViewModel.isProgressing,
        ) {
            AnimatedVisibility(visible = confirmViewModel.isConfirmSucceeded) {
                Text(
                    text = stringResource(id = R.string.ui_confirmScreen_backToLoginButton),
                    textAlign = TextAlign.Center,
                    fontSize = LoadingButtonConfig.TEXT_FONT_SIZE.sp
                )
            }
            AnimatedVisibility(visible = !confirmViewModel.isConfirmSucceeded) {
                Text(
                    text = stringResource(id = R.string.ui_confirmScreen_skipConfirmationButton),
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
    confirmViewModel.onConfirmSuccess()
    UrgentTheme {
        Surface {
            ConfirmScreen(
                confirmViewModel = confirmViewModel,
                userName = "Minh Nguyen",
                confirmMedium = "EMAIL",
                confirmDestination = "d**1@gmail.com",
                onConfirmRequest = {}
            )
        }
    }
}