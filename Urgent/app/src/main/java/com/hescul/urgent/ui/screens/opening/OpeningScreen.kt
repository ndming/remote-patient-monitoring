package com.hescul.urgent.ui.screens.opening

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.UrgentTheme
import java.util.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun OpeningScreen(
    openingViewModel: OpeningViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        openingViewModel.showOpening { onDone() }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.primary,
            letterSpacing = TextUnit(value = 10f, type = TextUnitType.Sp)
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
                openingViewModel = openingViewModel,
                onDone = {}
            )
        }
    }
}