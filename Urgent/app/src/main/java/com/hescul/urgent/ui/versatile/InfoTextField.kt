package com.hescul.urgent.ui.versatile

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme

/**
 * An enumerated type for [InfoTextField] metadata.
 *
 * @param label the label string displayed on the [InfoTextField]
 * @param hint the placeholder text displayed on the [InfoTextField]
 */
enum class InfoFieldType(@StringRes val label: Int, @StringRes val hint: Int) {
    NameField(R.string.ui_signUpScreen_userNameFieldLabel, R.string.ui_signUpScreen_userNameFieldHint),
    EmailField(R.string.ui_signUpScreen_emailFieldLabel, R.string.ui_signUpScreen_emailFieldHint),
    PasswordField(R.string.ui_signUpScreen_passwordFieldLabel, R.string.ui_signUpScreen_passwordFieldHint),
    ConfirmPasswordField(R.string.ui_signUpScreen_confirmPasswordFieldLabel, R.string.ui_signUpScreen_confirmPasswordFieldHint),
    ConfirmField(R.string.ui_confirmScreen_confirmFieldLabel, R.string.ui_confirmScreen_confirmFieldHint),
    DeviceIdField(R.string.ui_homeScreen_deviceIdFieldLabel, R.string.ui_homeScreen_deviceIdFieldHint)
}


/**
 * Styled [OutlinedTextField] for inputting an information field to the app.
 * @author Minh Nguyen
 *
 * @param text (state) current text to display
 * @param fieldType the [InfoFieldType] holds metadata for this text field
 * @param onTextChange (event) request the text change state
 * @param modifier the modifier for this element
 * @param enabled when set to false, the field is disabled
 * @param onImeAction (event) notify caller of [ImeAction.Done] events
 * @param isError when set to True, the [OutlinedTextField] will reacts to display the error state
 * @param visualTransform transforms the input text into different visuals
 * @param enableTrailingContent when set to true, the trailing action content will be available
 * @param trailingActionContent the content for trailing icon action
 * @param onTrailingAction callback when trailing action triggered
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfoTextField(
    text: String,
    fieldType: InfoFieldType,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldWidth: Dp = 280.dp,
    fieldHeight: Dp = 58.dp,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    visualTransform: VisualTransformation = VisualTransformation.None,
    enableTrailingContent: Boolean = false,
    trailingActionContent: @Composable () -> Unit = {},
    onTrailingAction: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = stringResource(id = fieldType.label))
        },
        placeholder = {
            Text(text = stringResource(fieldType.hint))
        },
        enabled = enabled,
        trailingIcon = {
            if (enableTrailingContent) {
                IconButton(onClick = onTrailingAction) {
                    trailingActionContent()
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        isError = isError,
        visualTransformation = visualTransform,
        modifier = modifier
            .width(fieldWidth)
            .height(fieldHeight)
    )
}

@Preview("Email Info Text Field")
@Composable
fun PreviewEmailInfoTextField() {
    UrgentTheme {
        Surface {
            InfoTextField(
                text = "example@gmail.com",
                fieldType = InfoFieldType.EmailField,
                onTextChange = {}
            )
        }
    }
}

@Preview("Password Info Text Field")
@Composable
fun PreviewPasswordInfoTextField() {
    UrgentTheme {
        Surface {
            InfoTextField(
                text = "example@gmail.com",
                fieldType = InfoFieldType.PasswordField,
                onTextChange = {},
                visualTransform = PasswordVisualTransformation(),
                enableTrailingContent = true,
                trailingActionContent = {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = "visibility off"
                    )
                }
            )
        }
    }
}
