package com.hescul.urgent.ui.screens.opening

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.ui.theme.UrgentTheme

@Composable
fun OpeningScreen(
    appName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = appName,
            style = MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        CircularProgressIndicator(
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Preview("Opening Screen")
@Composable
fun PreviewOpeningScreen() {
    val openingViewModel = OpeningViewModel()
    UrgentTheme {
        Surface {
            OpeningScreen(
                appName =  "Urgent",
            )
        }
    }
}