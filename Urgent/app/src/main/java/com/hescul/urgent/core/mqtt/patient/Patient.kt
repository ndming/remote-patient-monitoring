package com.hescul.urgent.core.mqtt.patient

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Patient(
    val deviceId: String,
    val name: String,
    val attributes: MutableList<PatientAttribute> = mutableListOf(),
    var picture: ImageVector = DEFAULT_PATIENT_PICTURE,
) {
    var status by mutableStateOf(DEFAULT_PATIENT_STATUS)
    var time by mutableStateOf(DEFAULT_UNKNOWN_VALUE)
        private set
    val data = listOf(
        PatientData(index = PatientIndex.Pulse),
        PatientData(index = PatientIndex.Spo2)
    )

    fun updateTimestamp(epoch: Long, locale: Locale) {
        time = TIME_FORMATTER.withLocale(locale).format(Instant.ofEpochSecond(epoch))
    }

    companion object {
        const val DEFAULT_UNKNOWN_VALUE = "--"
        val DEFAULT_PATIENT_PICTURE = Icons.Outlined.AccountCircle
        val DEFAULT_PATIENT_STATUS = PatientStatus.Offline
        val TIMESTAMP_ICON = Icons.Outlined.Schedule

        private val DEFAULT_ZONE = ZoneId.systemDefault()
        private val TIME_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(DEFAULT_ZONE)

        @Suppress("SpellCheckingInspection")
        val SAMPLE_PATIENT = Patient(
            deviceId = "RPMSOS0000",
            name = "Bling Bling",
            attributes = mutableListOf(
                PatientAttribute(key = "gender", value = "female", isPinned = true),
                PatientAttribute(key = "urgent level", value = "hot", isPinned = false),
                PatientAttribute(key = "age", value = "20", isPinned = true),
                PatientAttribute(key = "discharged date", value = "14/08", isPinned = false),
                PatientAttribute(key = "appointment", value = "20/08", isPinned = false),
                PatientAttribute(key = "illness", value = "cute", isPinned = true)
            )
        )
    }
}

data class PatientAttribute(
    var key: String,
    var value: String,
    var isPinned: Boolean = false
)

enum class PatientStatus(val code: Int, val tint: Color) {
    Online(code = 0, tint = OnlineColor),
    Offline(code = 1, tint = OfflineColor),
}

enum class PatientIndex(
    @StringRes val title: Int,
    @StringRes val unit: Int,
    val icon: ImageVector,
    val tint: Color,
    @StringRes val iconDescription: Int,
    val key: String,
) {
    Pulse(
        title = R.string.ui_profileScreen_pulseIndexName,
        unit = R.string.ui_profileScreen_pulseIndexUnit,
        icon = Icons.Default.Favorite,
        tint = PulseIconColor,
        iconDescription = R.string.cd_favoriteIcon,
        key = "pulse"
    ),
    Spo2(
        title = R.string.ui_profileScreen_spo2IndexName,
        unit = R.string.ui_profileScreen_spo2IndexUnit,
        icon = Icons.Default.Bloodtype,
        tint = Spo2IconColor,
        iconDescription = R.string.cd_bloodTypeIcon,
        key = "spo2"
    )
}

class PatientData(val index: PatientIndex) {
    var value by mutableStateOf(Patient.DEFAULT_UNKNOWN_VALUE)
}

