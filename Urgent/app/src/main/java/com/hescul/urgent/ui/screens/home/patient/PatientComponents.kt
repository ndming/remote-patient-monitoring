package com.hescul.urgent.ui.screens.home.patient

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.hescul.urgent.R
import com.hescul.urgent.core.mqtt.patient.Patient
import com.hescul.urgent.core.mqtt.patient.PatientAttribute
import com.hescul.urgent.core.mqtt.patient.PatientData
import com.hescul.urgent.core.mqtt.patient.PatientStatus
import com.hescul.urgent.ui.theme.UrgentTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun PatientChip(
    patient: Patient,
    onCLick: () -> Unit,
    modifier: Modifier = Modifier,
    chipPadding: Dp = 8.dp,
    enabled: Boolean = true,
) {
    Chip(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = chipPadding),
        onClick = onCLick,
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.secondaryVariant
        ),
        shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
        enabled = enabled
    ) {
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        PatientPictureSlot(
            picture = Patient.DEFAULT_PATIENT_PICTURE,
            status = patient.status
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = patient.name.ifBlank { patient.deviceId },
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.padding(vertical = 2.5.dp))
            PatientAttributeSlot(attributes = patient.attributes)
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            PatientDataSlot(
                data = patient.data,
                status = patient.status
            )
        }
    }
}

@Composable
private fun PatientPictureSlot(
    picture: ImageVector,
    status: PatientStatus,
    modifier: Modifier = Modifier
) {
    val avaSize = 70.dp
    Surface(
        shape = CircleShape,
        modifier = modifier.size(avaSize),
        border = BorderStroke(width = 2.dp, color = status.tint)
    ) {
        Image(
            imageVector = picture,
            contentDescription = stringResource(id = R.string.cd_patientPicture),
            contentScale = ContentScale.Fit,
            alpha = if (picture == Patient.DEFAULT_PATIENT_PICTURE) 0.15f else 0.9f
        )
    }
}

@Composable
fun PatientAttributeSlot(
    attributes: MutableList<PatientAttribute>,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.caption.fontSize
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        var passedFirst = false
        var isEmpty = true
        attributes.forEach { attribute ->
            if (attribute.pinned) {
                isEmpty = false
                if (!passedFirst) {
                    passedFirst = true
                }
                else {
                    Spacer(modifier = Modifier.padding(horizontal = 2.5.dp))
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = stringResource(id = R.string.cd_circleSeparateIcon),
                        modifier = Modifier
                            .size(5.dp)
                        ,
                        tint = MaterialTheme.colors.onSurface.copy(0.5f)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.5.dp))
                }
                Text(
                    text = attribute.value,
                    style = MaterialTheme.typography.caption.copy(fontSize = textSize),
                    color = MaterialTheme.colors.onSurface.copy(0.7f)
                )
            }
        }
        if (isEmpty) {
            Text(
                text = "No attributes pinned",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(0.7f)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PatientDataSlot(
    data: List<PatientData>,
    status: PatientStatus,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var passedFirstIndex = false
        val dataHorizontalPadding = 15.dp
        data.forEach { data ->
            if (!passedFirstIndex) {
                passedFirstIndex = true
            }
            else {
                Spacer(modifier = Modifier.padding(horizontal = dataHorizontalPadding))
            }
            val dataIconAlpha = if (status == PatientStatus.Online) 1.0f else 0.5f
            val dataIconSize = 22.dp
            Row(modifier = modifier) {
                Icon(
                    imageVector = data.index.icon,
                    tint = data.index.tint.copy(dataIconAlpha),
                    contentDescription = stringResource(id = data.index.iconDescription),
                    modifier = Modifier.size(dataIconSize)
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
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
                    val textColor = when (status) { // modify data color alpha based on status
                        PatientStatus.Online -> MaterialTheme.colors.onSurface
                        PatientStatus.Offline -> MaterialTheme.colors.onSurface.copy(0.2f)
                    }
                    Text(
                        text = data.value,
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        color = textColor,
                        modifier = Modifier.padding(top = 3.dp) // make data text aligned with icon
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun EmptyPatientGuide(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.ui_homeScreen_emptyPatientGuide),
        color = MaterialTheme.colors.onSurface.copy(0.6f),
        modifier = modifier.padding(10.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body1.copy(fontSize = TextUnit(20f, type = TextUnitType.Sp))
    )
}


@Preview
@Composable
private fun PreviewPatientChip() {
    val patient = Patient.SAMPLE_PATIENT
    patient.data[0].value = "79"
    patient.data[1].value = "98"
    patient.status = PatientStatus.Online
    UrgentTheme {
        Surface {
            PatientChip(
                patient = patient,
                onCLick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}