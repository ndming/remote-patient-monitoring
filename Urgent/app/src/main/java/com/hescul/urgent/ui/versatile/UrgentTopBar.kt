package com.hescul.urgent.ui.versatile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UrgentTopBar(
    title: String,
    showNavigateBack: Boolean,
    showMoreContentButton: Boolean,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    onNavigateBack: () -> Unit = {},
    enableNavigateBack: Boolean = false,
    enableMoreContent: Boolean = false,
    moreContent: @Composable ColumnScope.() -> Unit = {
        Text(
            text = stringResource(id = R.string.ui_doctorScreen_futurePromise),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = elevation
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            AnimatedContent(
                targetState = title,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(600)
                        ),
                        initialContentExit = fadeOut(
                            animationSpec = tween(0)
                        )
                    )
                }
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier) {
                IconButton(
                    onClick = { expanded = true },
                    enabled = showMoreContentButton && enableMoreContent
                ) {
                    if (showMoreContentButton) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.cd_moreVerticalIcon)
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 20.dp, y = 0.dp)
                ) {
                    moreContent()
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
            showMoreContentButton = true,
            enableMoreContent = true,
        )
    }
}