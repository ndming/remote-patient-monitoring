package com.hescul.urgent.ui.versatile

import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    textFontSize: TextUnit = 15.sp,
    buttonWidth: Dp = 280.dp,
    buttonHeight: Dp = 58.dp,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
    loadingWidth: Dp = 80.dp,
    loadingColor: Color = MaterialTheme.colors.onPrimary,
    transitionDuration: Int = 500,
    textEnterTransition: EnterTransition = fadeIn(animationSpec = TweenSpec(delay = 60)),
    textExitTransition: ExitTransition = ExitTransition.None,
    loadingEnterTransition: EnterTransition = EnterTransition.None,
    loadingExitTransition: ExitTransition = ExitTransition.None,
) {
    val width by animateDpAsState(
        targetValue = if (isLoading) loadingWidth else buttonWidth,
        animationSpec = TweenSpec(durationMillis = transitionDuration)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) buttonColors.backgroundColor(enabled = true).value
            else buttonColors.backgroundColor(enabled = false).value,
        animationSpec = TweenSpec(durationMillis = transitionDuration)
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .width(width)
            .height(buttonHeight),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = backgroundColor,
            contentColor = buttonColors.contentColor(enabled = true).value,
            disabledContentColor = buttonColors.contentColor(enabled = false).value
        ),
        enabled = enabled,
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = loadingEnterTransition,
            exit = loadingExitTransition
        ) {
            CircularProgressIndicator(
                color = loadingColor
            )
        }
        AnimatedVisibility(
            visible = !isLoading,
            enter = textEnterTransition,
            exit = textExitTransition
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = textFontSize
            )
        }
    }
}