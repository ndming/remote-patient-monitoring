package com.hescul.urgent.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.*
import kotlin.random.Random

class Patient(
    val deviceId: String,
    val name: String,
    val attributes: MutableList<PatientAttribute> = mutableListOf(),
    val data: MutableList<PatientData> = mutableListOf(
        PatientData(patientIndex = PatientIndex.Pulse),
        PatientData(patientIndex = PatientIndex.Spo2)
    ),
    var picture: ImageVector = DEFAULT_PATIENT_PICTURE,
    status: PatientStatus = DEFAULT_PATIENT_STATUS
) {
    var status by mutableStateOf(status)

    companion object {
        val DEFAULT_PATIENT_PICTURE = Icons.Outlined.AccountCircle
        val DEFAULT_PATIENT_STATUS = PatientStatus.Offline
        val SamplePatient0 = Patient(
            deviceId = "RPMSOS0000",
            name = "Online Patient",
            status = PatientStatus.Online,
            attributes = mutableListOf(
                PatientAttribute(
                    key = "Gender",
                    value = PatientGender.Male.name,
                    isPinned = true
                ),
                PatientAttribute(
                    key = "Age",
                    value = "22",
                    isPinned = true
                )
            ),
            data = mutableListOf(
                PatientData(PatientIndex.Pulse, Random.nextInt(60, 80), System.currentTimeMillis()),
                PatientData(PatientIndex.Spo2, Random.nextInt(90, 100), System.currentTimeMillis())
            )
        )
        val SamplePatient1 = Patient(
            deviceId = "RPMSOS0001",
            name = "Offline Patient",
            status = PatientStatus.Offline,
            attributes = mutableListOf(
                PatientAttribute(
                    key = "Gender",
                    value = PatientGender.Female.name,
                    isPinned = true
                ),
                PatientAttribute(
                    key = "Age",
                    value = "42",
                    isPinned = true
                ),
                PatientAttribute(
                    key = "Illness",
                    value = "Allergies",
                    isPinned = true
                )
            ),
            data = mutableListOf(
                PatientData(PatientIndex.Pulse, Random.nextInt(60, 80), System.currentTimeMillis()),
                PatientData(PatientIndex.Spo2, Random.nextInt(90, 100), System.currentTimeMillis())
            )
        )
        val SamplePatient2 = Patient(
            deviceId = "RPMSOS0002",
            name = " ",
            status = PatientStatus.Error,
            attributes = mutableListOf(
                PatientAttribute(
                    key = "Age",
                    value = "62",
                    isPinned = true
                ),
            ),
            data = mutableListOf(
                PatientData(PatientIndex.Pulse, Random.nextInt(60, 80), System.currentTimeMillis()),
                PatientData(PatientIndex.Spo2, Random.nextInt(90, 100), System.currentTimeMillis())
            )
        )
        val SamplePatient3 = Patient(
            deviceId = "RPMSOS0003",
            name = "A very very very long long long name",
            status = PatientStatus.Error,
            attributes = mutableListOf(
                PatientAttribute(
                    key = "Age",
                    value = "62",
                    isPinned = true
                ),
                PatientAttribute(
                    key = "Gender",
                    value = PatientGender.Female.name,
                    isPinned = true
                ),
            ),
            data = mutableListOf(
                PatientData(PatientIndex.Pulse, Random.nextInt(60, 80), System.currentTimeMillis()),
                PatientData(PatientIndex.Spo2, Random.nextInt(90, 100), System.currentTimeMillis())
            )
        )
        val SamplePatient4 = Patient(
            deviceId = "RPMSOS0004",
            name = "Indiana John",
            status = PatientStatus.Online,
            data = mutableListOf(
                PatientData(PatientIndex.Pulse, Random.nextInt(60, 80), System.currentTimeMillis()),
                PatientData(PatientIndex.Spo2, Random.nextInt(90, 100), System.currentTimeMillis())
            )
        )
    }
}

data class PatientAttribute(
    val key: String,
    val value: String,
    val isPinned: Boolean = false
)

enum class PatientGender {
    Male,
    Female
}

enum class PatientStatus(val code: Int, val icon: ImageVector, val tint: Color, @StringRes val iconDescription: Int) {
    Online(code = 0, icon = Icons.Default.Circle, tint = OnlineColor, iconDescription = R.string.cd_circleOnlineIcon),
    Offline(code = 1, icon = Icons.Default.Circle, tint = OfflineColor, iconDescription = R.string.cd_circleOfflineIcon),
    Error(code = 2, icon = Icons.Default.Circle, tint = ErrorColor, iconDescription = R.string.cd_circleErrorIcon)
}

enum class PatientIndex(
    @StringRes val indexName: Int,
    @StringRes val unit: Int,
    val icon: ImageVector,
    val tint: Color,
    @StringRes val iconDescription: Int,
    val hashKey: String,
) {
    Pulse(
        indexName = R.string.ui_profileScreen_pulseIndexName,
        unit = R.string.ui_profileScreen_pulseIndexUnit,
        icon = Icons.Default.Favorite,
        tint = PulseIconColor,
        iconDescription = R.string.cd_favoriteIcon,
        hashKey = "pulse"
    ),
    Spo2(
        indexName = R.string.ui_profileScreen_spo2IndexName,
        unit = R.string.ui_profileScreen_spo2IndexUnit,
        icon = Icons.Default.Bloodtype,
        tint = Spo2IconColor,
        iconDescription = R.string.cd_bloodTypeIcon,
        hashKey = "oxi"
    )
}

class PatientData(val patientIndex: PatientIndex, value: Int = -1, time: Long = -1L) {
    var value by mutableStateOf(value)
    var time by mutableStateOf(time)
}