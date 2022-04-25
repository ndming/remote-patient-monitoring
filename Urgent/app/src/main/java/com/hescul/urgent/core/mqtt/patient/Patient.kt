package com.hescul.urgent.core.mqtt.patient

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
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
    name: String
) {
    var name by mutableStateOf(name)
    var status by mutableStateOf(DEFAULT_PATIENT_STATUS)
    var time by mutableStateOf(DEFAULT_UNKNOWN_VALUE)
        private set
    val data = listOf(
        PatientData(index = PatientIndex.Pulse, initialValue = DEFAULT_UNKNOWN_VALUE),
        PatientData(index = PatientIndex.Spo2, initialValue = DEFAULT_UNKNOWN_VALUE)
    )
    val attributes: MutableList<PatientAttribute> = mutableListOf()

    fun updateTimestamp(epoch: Long, locale: Locale) {
        time = TIME_FORMATTER.withLocale(locale).format(Instant.ofEpochSecond(epoch)).substringBefore('+').trimEnd()
    }

    companion object {
        const val DEFAULT_UNKNOWN_VALUE = "--"
        val DEFAULT_PATIENT_PICTURE = Icons.Outlined.AccountCircle
        val DEFAULT_PATIENT_STATUS = PatientStatus.Offline

        private val DEFAULT_ZONE = ZoneId.systemDefault()
        private val TIME_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(DEFAULT_ZONE)

        const val MAXIMUM_ATTRIBUTES_ALLOWED = 10
        const val MAXIMUM_PINNED_ATTRIBUTES_ALLOWED = 3

        @Suppress("SpellCheckingInspection")
        val SAMPLE_PATIENT = Patient(
            deviceId = "RPMSOS0000",
            name = "Bling Bling"
        )

        val UNKNOWN_PATIENT = Patient(
            deviceId = DEFAULT_UNKNOWN_VALUE,
            name = DEFAULT_UNKNOWN_VALUE
        )
    }
}

class PatientAttribute(key: String, value: String, pinned: Boolean = false) {
    var key by mutableStateOf(key)
    var value by mutableStateOf(value)
    var pinned by mutableStateOf(pinned)

    fun clone() = PatientAttribute(key, value, pinned)
}

enum class PatientStatus(val code: Int, val tint: Color) {
    Online(code = 0, tint = OnlineColor),
    Offline(code = 1, tint = OfflineColor),
}

enum class PatientIndex(
    @StringRes val label: Int,
    @StringRes val unit: Int,
    val icon: ImageVector,
    val tint: Color,
    @StringRes val iconDescription: Int,
    val key: String,
) {
    Pulse(
        label = R.string.ui_profileScreen_pulseIndexName,
        unit = R.string.ui_profileScreen_pulseIndexUnit,
        icon = Icons.Default.Favorite,
        tint = PulseIconColor,
        iconDescription = R.string.cd_favoriteIcon,
        key = "pulse"
    ),
    Spo2(
        label = R.string.ui_profileScreen_spo2IndexName,
        unit = R.string.ui_profileScreen_spo2IndexUnit,
        icon = Icons.Default.Bloodtype,
        tint = Spo2IconColor,
        iconDescription = R.string.cd_bloodTypeIcon,
        key = "spo2"
    )
}

class PatientData(val index: PatientIndex, initialValue: String) {
    var value by mutableStateOf(initialValue)
}

