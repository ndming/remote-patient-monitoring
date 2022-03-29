package com.hescul.urgent.ui.screens.confirm

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.Green800
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.InfoFieldType
import com.hescul.urgent.ui.versatile.InfoTextField
import com.hescul.urgent.ui.versatile.LoadingButton
import com.hescul.urgent.ui.versatile.config.LoadingButtonConfig
import com.hescul.urgent.ui.versatile.config.ViewConfig

@Composable
fun ConfirmCodeField(
    text: String,
    onTextChange: (String) -> Unit,
    confirmMedium: String,
    confirmDestination: String,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enableEdit: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${stringResource(id = R.string.ui_confirmScreen_confirmInstruction)} $confirmMedium\n($confirmDestination)",
            textAlign = TextAlign.Center,
        )
        InfoTextField(
            text = text,
            fieldType = InfoFieldType.ConfirmField,
            onTextChange = onTextChange,
            enabled = enableEdit,
            keyboardType = KeyboardType.Number,
            onImeAction = onConfirmRequest,
            isError = isError
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConfirmButton(
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier,
    buttonEnable: Boolean = true,
    isProgressing: Boolean = false,
    isConfirmSucceeded: Boolean = false
) {
    LoadingButton(
        text = if (!isConfirmSucceeded)
            stringResource(id = R.string.ui_confirmScreen_confirmButton)
        else
            stringResource(id = R.string.ui_confirmScreen_confirmSuccessMessage),
        buttonColors = if (!isConfirmSucceeded)
            ButtonDefaults.buttonColors()
        else ButtonDefaults.buttonColors(
            backgroundColor = Green800,
            contentColor = MaterialTheme.colors.onPrimary
        ),
        onClick = onConfirmRequest,
        modifier = modifier,
        enabled = buttonEnable,
        isLoading = isProgressing,
        textFontSize = LoadingButtonConfig.TEXT_FONT_SIZE.sp,
        buttonWidth = ViewConfig.TEXT_FIELD_DEFAULT_WIDTH.dp,
        buttonHeight = ViewConfig.TEXT_FIELD_DEFAULT_HEIGHT.dp,
        loadingWidth = ViewConfig.CIRCULAR_PROGRESS_INDICATOR_DEFAULT_WIDTH.dp,
        transitionDuration = LoadingButtonConfig.STATE_TRANSITION_DURATION,
        textEnterTransition = fadeIn(animationSpec = TweenSpec(delay = LoadingButtonConfig.TEXT_FADE_IN_DELAY)),
    )
}

@Preview(
    name = "Confirm Code Field"
)
@Composable
fun PreviewConfirmCodeField() {
    UrgentTheme {
        Surface {
            ConfirmCodeField(
                text = "255456",
                onTextChange = {},
                confirmMedium = "EMAIL",
                confirmDestination = "d***1@gmail.com",
                onConfirmRequest = {}
            )
        }
    }
}

@Preview(
    name = "Confirm Button"
)
@Composable
fun PreviewConfirmButton() {
    UrgentTheme {
        Surface {
            ConfirmButton(
                onConfirmRequest = {}
            )
        }
    }
}