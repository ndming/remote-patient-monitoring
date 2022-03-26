package com.hescul.urgent.ui.versatile

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.hescul.urgent.ui.theme.UrgentTheme

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
 * @param maxLines maximum number of input lines
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
    enabled: Boolean = true,
    onImeAction: () -> Unit = {},
    maxLines: Int = 1,
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
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
                keyboardController?.hide()
            }
        ),
        maxLines = maxLines,
        isError = isError,
        visualTransformation = visualTransform,
        modifier = modifier
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
