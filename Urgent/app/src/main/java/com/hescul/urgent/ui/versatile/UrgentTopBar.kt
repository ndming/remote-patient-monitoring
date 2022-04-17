package com.hescul.urgent.ui.versatile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme

@Composable
fun UrgentTopBar(
    title: String,
    showNavigateBack: Boolean,
    showRightAction: Boolean,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    enableNavigateBack: Boolean = false,
    onRightAction: () -> Unit = {},
    enableRightAction: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(
                    onClick = onNavigateBack,
                    enabled = showNavigateBack && enableNavigateBack
                ) {
                    if (showNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIos,
                            contentDescription = stringResource(id = R.string.cd_arrowBackIosIcon)
                        )
                    }
                }
            }
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
            )
            Row {
                IconButton(
                    onClick = onRightAction,
                    enabled = showRightAction && enableRightAction
                ) {
                    if (showRightAction) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.cd_moreVerticalIcon)
                        )
                    }
                }
            }
        }
    }
}

@Preview("Urgent App Top Bar")
@Composable
fun PreviewUrgentTopBar() {
    UrgentTheme {
        UrgentTopBar(
            title = "Home",
            onNavigateBack = {},
            showNavigateBack = true,
            enableNavigateBack = true,
            showRightAction = true,
            enableRightAction = true,
            onRightAction = {}
        )
    }
}