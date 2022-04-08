package com.hescul.urgent.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme

@Composable
fun LoginHeader(
    modifier: Modifier = Modifier,
    innerPadding: Dp = 5.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.ui_loginScreen_header),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )
        Text(
            text = stringResource(id = R.string.ui_loginScreen_subHeader),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(0.6f),
            modifier = Modifier.padding(vertical = innerPadding)
        )
    }
}

@Composable
fun LoginFooter(
    message: String,
    actionText: String,
    onAction: () -> Unit,
    actionEnable: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message
        )
        TextButton(
            onClick = onAction,
            enabled = actionEnable
        ) {
            Text(text = actionText)
        }
    }
}

@Preview("Login Header")
@Composable
fun PreviewLoginHeader() {
    UrgentTheme {
        Surface {
            LoginHeader()
        }
    }
}

@Preview("Login Footer")
@Composable
fun PreviewLoginFooter() {
    UrgentTheme {
        Surface {
            LoginFooter(
                message = "Don't have an account?",
                actionText = "Sign Up",
                onAction = {},
                actionEnable = true,
            )
        }
    }
}