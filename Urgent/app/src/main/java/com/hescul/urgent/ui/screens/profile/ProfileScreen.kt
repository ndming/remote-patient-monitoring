package com.hescul.urgent.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.core.mqtt.patient.Patient
import com.hescul.urgent.core.mqtt.patient.PatientAttribute
import com.hescul.urgent.core.mqtt.patient.PatientStatus
import com.hescul.urgent.ui.screens.home.patient.PatientViewModel
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.UrgentTopBar

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    patientViewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize()) {
        UrgentTopBar(
            title = stringResource(id = R.string.ui_profileScreen_title),
            showNavigateBack = true,
            showMoreContentButton = true,
            onNavigateBack = onNavigateBack,
            enableNavigateBack = true,
            enableMoreContent = true,
            elevation = if (scrollState.value == 0) 0.dp else 1.dp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val contentPadding = 20.dp
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            PatientProfileCard(patient = profileViewModel.patient)
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
            PatientProfileIndex(
                profileViewModel.patient.deviceId,
                numOfSensors = profileViewModel.patient.data.size,
                timestamp = profileViewModel.patient.time
            )
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
            profileViewModel.patient.data.forEach { data ->
                PatientProfileData(data = data)
                Spacer(modifier = Modifier.padding(vertical = contentPadding))
            }
            PatientProfileAttributes(
                name = profileViewModel.patient.name,
                attributes = profileViewModel.patient.attributes
            )
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onNavigateBack()
                    patientViewModel.onDeletePatientRequest(
                        patient = profileViewModel.patient,
                        onDone = {
                            profileViewModel.reset()
                        }
                    )
                },
                enabled = patientViewModel.isConnected && !profileViewModel.isProgressing,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 10.dp, top = 12.5.dp),
                    text = stringResource(id = R.string.ui_profileScreen_deletePatientButton),
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.padding(vertical = contentPadding))
        }
    }
}

@Preview
@Composable
private fun PreviewProfileScreen() {
    val attributes = listOf(
        PatientAttribute(key = "Gender", value = "Female", true),
        PatientAttribute(key = "Age", value = "21", true),
        PatientAttribute(key = "Illness", value = "Cute", true),
        PatientAttribute(key = "Alias", value = "Cutie"),
        PatientAttribute(key = "Date", value = "Waiting")
    )
    val patient = Patient.SAMPLE_PATIENT
    patient.status = PatientStatus.Offline
    patient.attributes.addAll(elements = attributes)
    val profileViewModel = ProfileViewModel()
    profileViewModel.updatePatient(patient)
    val patientViewModel = PatientViewModel(LocalContext.current)
    UrgentTheme {
        Surface {
            ProfileScreen(
                profileViewModel = profileViewModel,
                patientViewModel = patientViewModel,
                onNavigateBack = {},
            )
        }
    }
}