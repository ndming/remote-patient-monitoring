package com.hescul.urgent.ui.screens.home.doctor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.core.mqtt.doctor.Doctor
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DoctorScreen(
    doctorViewModel: DoctorViewModel,
    scrollState: ScrollState,
    connected: Boolean,
    onNavigateBack: () -> Unit,
    onSignOutDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current
    BackHandler(enabled = !doctorViewModel.isProgressing) {
        onNavigateBack()
    }
    DoctorSignOutAlertDialog(
        showAlertDialog = doctorViewModel.showSignOutAlertDialog,
        onConfirmSignOut = {
            doctorViewModel.onSignOutConfirm(localContext, onSignOutDone)
        },
        onDismissRequest = doctorViewModel::onSignOutDismiss
    )
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // progress holder
        AnimatedVisibility(visible = doctorViewModel.isProgressing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
        }
        // status holder
        AnimatedVisibility(visible = doctorViewModel.status.isNotEmpty()) {
            AnimatedContent(
                targetState = doctorViewModel.status,
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
                    text = doctorViewModel.status,
                    textAlign = TextAlign.Center,
                    color = if (doctorViewModel.isStatusError) MaterialTheme.colors.error
                    else MaterialTheme.colors.onSurface.copy(0.7f)
                )
            }
        }
        // content
        if (doctorViewModel.isLaunched) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val contentPadding = 10.dp
                Spacer(modifier = Modifier.padding(vertical = contentPadding))
                DoctorPicture(connected = connected)
                Spacer(modifier = Modifier.padding(contentPadding))
                Text(
                    text = doctorViewModel.doctorAttributes[Doctor.Attribute.Name.key]
                        ?: Doctor.DEFAULT_UNKNOWN_VALUE,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                )
                Spacer(modifier = Modifier.padding(contentPadding))
                Doctor.Option.values().forEach { option ->
                    DoctorOption(
                        option = option,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when(option) {
                            Doctor.Option.Account -> {
                                AccountContent(
                                    attributes = doctorViewModel.doctorAttributes,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Doctor.Option.Settings -> {
                                Text(
                                    text = stringResource(id = R.string.ui_doctorScreen_futurePromise),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Doctor.Option.Help -> {
                                Text(
                                    text = stringResource(id = R.string.ui_doctorScreen_futurePromise),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(contentPadding))
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        doctorViewModel.onSignOutRequest()
                    },
                    enabled = !doctorViewModel.isProgressing,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp, top = 12.5.dp),
                        text = stringResource(id = R.string.ui_doctorScreen_signOutButton),
                        style = MaterialTheme.typography.h5.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDoctorScreen() {
    val doctorViewModel = DoctorViewModel()
    UrgentTheme {
        Surface {
            DoctorScreen(
                doctorViewModel = doctorViewModel,
                connected = true,
                onNavigateBack = {},
                onSignOutDone = {},
                scrollState = ScrollState(0)
            )
        }
    }
}