package com.hescul.urgent.ui.screens.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.UrgentTopBar
import com.hescul.urgent.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val contentPadding = 12.dp
    val patientListState = rememberLazyListState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UrgentTopBar(
                title = stringResource(id = R.string.ui_homeScreen_title),
                onLeftActionClick = { /*TODO*/ },
                onRightActionClick = { /*TODO*/ },
            )
        },
        floatingActionButton = {
            HomeFAB(
                onCLick = homeViewModel::onAddPatient,
                extended = patientListState.firstVisibleItemScrollOffset == 0
            )
        }
    ) {
        LazyColumn(
            state = patientListState,
            contentPadding = PaddingValues(
                horizontal = contentPadding * 2,
                vertical = contentPadding
            ),
        ) {
            item { 
                Text(
                    text = stringResource(id = R.string.ui_homeScreen_contentTitle),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = contentPadding / 2)
                )
            }
            items(
                items = homeViewModel.patients,
                key = {
                    it.deviceId
                }
            ) { patient ->
                PatientChip(
                    patient = patient,
                    onCLick = { /*TODO*/ },
                    chipPadding = contentPadding,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(400)
                    )
                )
            }
        }
    }
}

@Preview("Home Screen")
@Composable
fun PreviewHomeScreen() {
    val homeViewModel = HomeViewModel()
    UrgentTheme {
        Surface {
            HomeScreen(
                homeViewModel = homeViewModel,
            )
        }
    }
}