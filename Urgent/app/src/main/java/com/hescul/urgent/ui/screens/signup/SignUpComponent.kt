package com.hescul.urgent.ui.screens.signup

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.InfoFieldType
import com.hescul.urgent.ui.versatile.InfoTextField


@Composable
fun UserNameField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enableEdit: Boolean = true,
) {
    InfoTextField(
        text = text,
        fieldType = InfoFieldType.NameField,
        onTextChange = onTextChange,
        isError = isError,
        modifier = modifier,
        enabled = enableEdit
    )
}

@Composable
fun EmailField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enableEdit: Boolean = true,
) {
    InfoTextField(
        text = text,
        fieldType = InfoFieldType.EmailField,
        onTextChange = onTextChange,
        isError = isError,
        modifier = modifier,
        enabled = enableEdit
    )
}

@Composable
fun PasswordField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enableEdit: Boolean = true,
) {
    var isVisibilityTriggered by remember { mutableStateOf(false) }
    val trailingActionIcon = if (isVisibilityTriggered) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
    val contentDescription = if (isVisibilityTriggered) R.string.cd_visibilityIcon else R.string.cd_visibilityOffIcon
    val visualTransform = if (isVisibilityTriggered) VisualTransformation.None else PasswordVisualTransformation()
    val enableTrailingAction = text.isNotEmpty()

    InfoTextField(
        text = text,
        fieldType = InfoFieldType.PasswordField,
        onTextChange = onTextChange,
        modifier = modifier,
        visualTransform = visualTransform,
        enabled = enableEdit,
        enableTrailingContent = enableTrailingAction,
        trailingActionContent = {
            if (enableTrailingAction) {
                Icon(
                    imageVector = trailingActionIcon,
                    contentDescription = contentDescription.toString()
                )
            }
        },
        onTrailingAction = {
            if (enableTrailingAction) {
                isVisibilityTriggered = !isVisibilityTriggered
            }
        },
        isError = isError,
    )
}

@Composable
fun ConfirmPasswordField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enableEdit: Boolean = true,
) {
    var isVisibilityTriggered by remember { mutableStateOf(false) }
    val trailingActionIcon = if (isVisibilityTriggered) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
    val contentDescription = if (isVisibilityTriggered) R.string.cd_visibilityIcon else R.string.cd_visibilityOffIcon
    val visualTransform = if (isVisibilityTriggered) VisualTransformation.None else PasswordVisualTransformation()
    val enableTrailingAction = text.isNotEmpty()

    InfoTextField(
        text = text,
        fieldType = InfoFieldType.ConfirmPasswordField,
        onTextChange = onTextChange,
        modifier = modifier,
        visualTransform = visualTransform,
        enabled = enableEdit,
        enableTrailingContent = enableTrailingAction,
        trailingActionContent = {
            if (enableTrailingAction) {
                Icon(
                    imageVector = trailingActionIcon,
                    contentDescription = contentDescription.toString()
                )
            }
        },
        onTrailingAction = {
            if (enableTrailingAction) {
                isVisibilityTriggered = !isVisibilityTriggered
            }
        },
        isError = isError
    )
}

@Composable
fun SignUpHeader(
    modifier: Modifier = Modifier,
    headerStartPadding: Dp = 20.dp,
    headerInnerPadding: Dp = 5.dp,
    headerEndPadding: Dp = 10.dp,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = headerStartPadding))
        Text(
            text = "Register Account",
            style = MaterialTheme.typography.h4,
        )
        Text(
            text = "Complete your details or continue\n" +
                    "with social media",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = headerInnerPadding)
        )
        Spacer(modifier = Modifier.padding(vertical = headerEndPadding))
    }
}

@Composable
fun SignUpFooter(
    modifier: Modifier = Modifier,
    footerPadding: Dp = 5.dp
) {
    Text(
        text = "By continuing you confirm that you agree\n" +
                    "with our Term and Condition",
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        modifier = modifier.padding(vertical = footerPadding),
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpButton(
    onSignUp: (context: Context) -> Unit,
    modifier: Modifier = Modifier,
    buttonEnable: Boolean = true,
    isProgressing: Boolean = false,
) {
    val localContext = LocalContext.current
    Button(
        onClick = { onSignUp(localContext) },
        modifier = modifier,
        enabled = buttonEnable
    ) {
        Row {
            AnimatedVisibility(visible = !isProgressing) {
                Text(
                    text =  stringResource(id = R.string.ui_signUpScreen_signUpButton),
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp
                        )
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
            AnimatedVisibility(visible = isProgressing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignUpHeader() {
    UrgentTheme {
        Surface {
            SignUpHeader()
        }
    }
}

@Preview
@Composable
fun PreviewSignUpFooter() {
    UrgentTheme {
        Surface {
            SignUpFooter()
        }
    }
}