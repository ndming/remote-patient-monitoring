package com.hescul.urgent.ui.screens.home.patient

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hescul.urgent.R
import com.hescul.urgent.core.mqtt.patient.Patient
import com.hescul.urgent.ui.theme.UrgentTheme


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PatientScreen(
    patientViewModel: PatientViewModel,
    patientListState: LazyListState,
    onPatientSelect: (Patient) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // progress holder
        AnimatedVisibility(visible = patientViewModel.isProgressing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
        }
        // status holder
        AnimatedVisibility(visible = patientViewModel.status.isNotEmpty()) {
            AnimatedContent(
                targetState = patientViewModel.status,
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
                    text = patientViewModel.status,
                    textAlign = TextAlign.Center,
                    color = if (patientViewModel.isStatusError) MaterialTheme.colors.error
                    else MaterialTheme.colors.onSurface.copy(0.7f)
                )
            }
        }

        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing = patientViewModel.isRefreshing),
            onRefresh = { patientViewModel.onRefreshRequest() },
            swipeEnabled = !patientViewModel.isProgressing && !patientViewModel.isRefreshing,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.primary
                )
            }
        ) {
            // patient content
            if (patientViewModel.isLaunched) {
                if (patientViewModel.patients.isNotEmpty()) {
                    PatientProfiles(
                        patients = patientViewModel.patients,
                        listState = patientListState,
                        modifier = Modifier.fillMaxSize(),
                        enabled = !patientViewModel.isProgressing && !patientViewModel.isRefreshing,
                        onPatientSelect = onPatientSelect
                    )
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyPatientGuide()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PatientProfiles(
    patients: List<Patient>,
    listState: LazyListState,
    enabled: Boolean,
    onPatientSelect: (Patient) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentPadding = 12.dp
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = contentPadding * 2,
            vertical = contentPadding
        ),
        modifier = modifier
    ) {
        item {
            Text(
                text = stringResource(id = R.string.ui_homeScreen_contentTitle),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = contentPadding / 2)
            )
        }
        items(
            items = patients,
            key = { it.deviceId }
        ) { patient -> PatientChip(
            patient = patient,
            onCLick = { onPatientSelect(patient) },
            chipPadding = contentPadding,
            modifier = Modifier.animateItemPlacement(
                animationSpec = tween(200)
            ),
            enabled = enabled
        )
        }
    }
}

@Preview
@Composable
private fun PreviewPatientScreen() {
    val patientViewModel = PatientViewModel(LocalContext.current)
    UrgentTheme {
        Surface {
            PatientScreen(
                patientViewModel = patientViewModel,
                patientListState = rememberLazyListState(),
                onPatientSelect = {}
            )
        }
    }
}