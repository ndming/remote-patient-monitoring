package com.hescul.urgent.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R
import com.hescul.urgent.UrgentApplication
import com.hescul.urgent.core.mqtt.doctor.Doctor
import com.hescul.urgent.core.mqtt.patient.*
import com.hescul.urgent.ui.screens.home.patient.PatientAttributeSlot
import com.hescul.urgent.ui.theme.UrgentTheme
import java.util.*

private val headerPadding = 8.dp
private val attributes = listOf(
    PatientAttribute(key = "Gender", value = "Female", true),
    PatientAttribute(key = "Age", value = "21", true),
    PatientAttribute(key = "Illness", value = "Cute", true),
    PatientAttribute(key = "Alias", value = "Cutie"),
    PatientAttribute(key = "Date", value = "Waiting")
)
@Composable
private fun PatientProfileHeader(
    header: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = header,
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
fun PatientProfileCard(
    patient: Patient,
    modifier: Modifier = Modifier
) {
    val cardColor = when(patient.status) {
        PatientStatus.Online -> MaterialTheme.colors.secondary
        PatientStatus.Offline -> MaterialTheme.colors.secondaryVariant
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = cardColor,
        contentColor = cardColor,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(width = 0.dp, cardColor),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avaSize = 70.dp
            Surface(
                shape = CircleShape,
                modifier = modifier.size(avaSize),
                border = BorderStroke(width = 1.dp, color = Color.White)
            ) {
                Image(
                    imageVector = Doctor.DEFAULT_DOCTOR_PICTURE,
                    contentDescription = stringResource(id = R.string.cd_patientPicture),
                    contentScale = ContentScale.Fit,
                    alpha = 0.15f
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text = patient.name.ifBlank { patient.deviceId },
                    style = MaterialTheme.typography.h6.copy(fontSize = 24.sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines =  1
                )
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
                PatientAttributeSlot(
                    attributes = patient.attributes,
                    textSize = 20.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPatientProfileCard() {
    val patient = Patient.SAMPLE_PATIENT
    patient.status = PatientStatus.Offline
    patient.attributes.addAll(
        elements = attributes
    )
    UrgentTheme {
        Surface {
            PatientProfileCard(
                patient = patient
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PatientProfileData(
    data: PatientData,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PatientProfileHeader(header = stringResource(id = data.index.label) + " " + stringResource(id = R.string.ui_profileScreen_indexDataHeader))
        Spacer(modifier = Modifier.padding(vertical = headerPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = data.value,
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
                    text = data.value,
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 2.5.dp))
            Column {
                Icon(
                    imageVector = data.index.icon,
                    contentDescription = stringResource(id = data.index.iconDescription),
                    tint = data.index.tint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = stringResource(id = data.index.unit),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPatientProfileData() {
    val data = PatientData(index = PatientIndex.Pulse, initialValue = "85")
    UrgentTheme {
        Surface {
            PatientProfileData(data = data)
        }
    }
}

@Composable
fun PatientProfileAttributes(
    name: String,
    attributes: List<PatientAttribute>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PatientProfileHeader(header = stringResource(id = R.string.ui_profileScreen_attributesHeader))
        Spacer(modifier = Modifier.padding(vertical = headerPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.ui_profileScreen_attributeName),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface.copy(0.4f)
            )
            Text(
                text = name.ifBlank { Patient.DEFAULT_UNKNOWN_VALUE },
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 2.5.dp))
        attributes.forEach { attribute ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = attribute.key.ifBlank { Patient.DEFAULT_UNKNOWN_VALUE }
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onSurface.copy(0.4f)
                )
                Text(
                    text = attribute.value.ifBlank { Patient.DEFAULT_UNKNOWN_VALUE },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPatientProfileAttributes() {
    UrgentTheme {
        Surface {
            PatientProfileAttributes(
                attributes = attributes,
                name = ""
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PatientProfileIndex(
    numOfSensors: Int,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PatientProfileHeader(header = stringResource(id = R.string.ui_profileScreen_indexHeader))
        Spacer(modifier = Modifier.padding(vertical = headerPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.ui_profileScreen_numOfSensorsEntry),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface.copy(0.4f)
            )
            Text(
                text = numOfSensors.toString(),
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.ui_profileScreen_lastUpdateEntry),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface.copy(0.4f)
            )
            AnimatedContent(
                targetState = timestamp,
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
                    text = timestamp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPatientProfileIndex() {
    val patient = Patient.SAMPLE_PATIENT
    patient.updateTimestamp(System.currentTimeMillis() / 1000, locale = Locale.ENGLISH)
    UrgentTheme {
        Surface {
            PatientProfileIndex(
                numOfSensors = 2,
                timestamp = patient.time
            )
        }
    }
}