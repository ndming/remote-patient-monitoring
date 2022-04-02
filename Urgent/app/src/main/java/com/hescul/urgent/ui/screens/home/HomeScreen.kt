package com.hescul.urgent.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.UrgentTopBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        UrgentTopBar(
            title = "Home",
            onNavigateBack = { /*TODO*/ },
            onLeftActionClick = { /*TODO*/ },
            onRightActionClick = { /*TODO*/ }
        )
    }
}

@Preview("Home Screen")
@Composable
fun PreviewHomeScreen() {
    UrgentTheme {
        Surface {
            HomeScreen()
        }
    }
}