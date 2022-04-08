package com.hescul.urgent.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.ErrorColor
import com.hescul.urgent.ui.theme.UrgentTheme

@Composable
fun HomeFAB(
    onCLick: () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = false,
) {
    FloatingActionButton(
        onClick = onCLick,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Row(
            modifier = modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.cd_addIcon)
            )
            AnimatedVisibility(
                visible = extended,
            ) {
                Text(
                    text = stringResource(id = R.string.ui_homeScreen_fabExtended),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun PatientChip(
    patient: Patient,
    onCLick: () -> Unit,
    modifier: Modifier = Modifier,
    chipPadding: Dp = 8.dp,
) {
    val chipColor = MaterialTheme.colors.onBackground.copy(0.05f)
    Chip(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = chipPadding),
        onClick = onCLick,
        colors = ChipDefaults.chipColors(
            backgroundColor = chipColor
        ),
        shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
    ) {
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        val avaSize = 70.dp
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(avaSize)
            ,
            border = BorderStroke(width = 2.dp, color = patient.status.tint)
        ) {
            Image(
                imageVector = patient.picture,
                contentDescription = stringResource(id = R.string.cd_patientPicture),
                contentScale = ContentScale.Fit,
                alpha = if (patient.picture == Patient.DEFAULT_PATIENT_PICTURE) 0.15f else 0.9f
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = patient.name.ifBlank { patient.deviceId },
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.padding(vertical = 2.5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var passedFirst = false
                var isEmpty = true
                patient.attributes.forEach { attribute ->
                    if (attribute.isPinned) {
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
                            style = MaterialTheme.typography.caption,
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
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            val dataRowPadding = 15.dp
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var passedFirstData = false
                val iconAlpha = if (patient.status == PatientStatus.Online) 1.0f else 0.5f
                patient.data.forEach { data ->
                    if (!passedFirstData) {
                        passedFirstData = true
                    }
                    else {
                        Spacer(modifier = Modifier.padding(horizontal = dataRowPadding))
                    }
                    Icon(
                        imageVector = data.patientIndex.icon,
                        tint = data.patientIndex.tint.copy(iconAlpha),
                        contentDescription = stringResource(id = data.patientIndex.iconDescription),
                        modifier = Modifier.size(20.dp)
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
                        val textColor = when(patient.status) {
                            PatientStatus.Online -> MaterialTheme.colors.onSurface
                            PatientStatus.Offline -> MaterialTheme.colors.onSurface.copy(0.2f)
                            PatientStatus.Error -> ErrorColor.copy(0.7f)
                        }
                        Text(
                            text = if (data.value < 0) "??" else data.value.toString(),
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            color = textColor,
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        
    }
}

@Preview
@Composable
private fun PreviewHomeFAB() {
    UrgentTheme {
        Surface {
            HomeFAB(
                onCLick = {},
                extended = true
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPatientChip() {
    val patient = Patient.SamplePatient0
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