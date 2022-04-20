package com.hescul.urgent.ui.screens.home.doctor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hescul.urgent.R
import com.hescul.urgent.core.mqtt.doctor.Doctor
import com.hescul.urgent.ui.theme.UrgentTheme

@Composable
fun DoctorPicture(
    connected: Boolean,
    modifier: Modifier = Modifier
) {
    val avaSize = 100.dp
    Surface(
        shape = CircleShape,
        modifier = modifier.size(avaSize),
        border = BorderStroke(
            width = 2.dp,
            color = if (connected) Doctor.Status.Connected.tint else Doctor.Status.Disconnected.tint
        )
    ) {
        Image(
            imageVector = Doctor.DEFAULT_DOCTOR_PICTURE,
            contentDescription = stringResource(id = R.string.cd_patientPicture),
            contentScale = ContentScale.Fit,
            alpha = 0.15f
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DoctorOption(
    option: Doctor.Option,
    modifier: Modifier = Modifier,
    expandContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var expanded by remember { mutableStateOf(false) }
        Chip(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = !expanded },
            colors = ChipDefaults.chipColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant
            ),
            shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconSize = 40.dp
                    Icon(
                        imageVector = option.icon,
                        contentDescription = stringResource(id = option.iconDescription),
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Text(
                        text = stringResource(id = option.title),
                        style = MaterialTheme.typography.h5,
                        color = Color.Black
                    )
                }
                Icon(
                    imageVector = if(expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = stringResource(
                        id = if (expanded) R.string.cd_expandLessIcon else R.string.cd_expandMoreIcon
                    )
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            expandContent()
        }
    }
}

@Composable
fun AccountContent(
    attributes: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        attributes.forEach { (key, value) ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    text = Doctor.Attribute.values().find { it.key == key }?.translate?.let { stringResource(id = it) } ?: key
                )
                Text(
                    text = value.ifBlank { Doctor.DEFAULT_UNKNOWN_VALUE },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDoctorOption() {
    UrgentTheme {
        Surface {
            DoctorOption(
                option = Doctor.Option.Account,
                expandContent = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewAccountContent() {
    @Suppress("SpellCheckingInspection")
    val attributes = hashMapOf(
        Pair("name", "Jessica Johnson"),
        Pair("email", "example@email.com"),
        Pair("sub", "55e0a-afaf56-afadfsd-afda54")
    )
    UrgentTheme {
        Surface {
            AccountContent(
                attributes = attributes
            )
        }
    }
}