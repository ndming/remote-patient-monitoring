package com.hescul.urgent.ui.screens.home.patient

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.hescul.urgent.UrgentApplication
import com.hescul.urgent.core.cognito.CognitoAuthenticator
import com.hescul.urgent.core.cognito.CognitoConfig
import com.hescul.urgent.core.mqtt.doctor.MqttDoctorClient
import com.hescul.urgent.core.mqtt.MqttBrokerConfig
import com.hescul.urgent.core.mqtt.MqttClientConfig
import com.hescul.urgent.core.mqtt.patient.*
import com.hescul.urgent.core.utils.InfoValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class PatientViewModel(context: Context) : ViewModel() {
    private val credentialsProvider = CognitoCachingCredentialsProvider(context, CognitoConfig.IDENTITY_POOL_ID, CognitoConfig.IDENTITY_POOL_REGION)
    private lateinit var mqttDoctorClient: MqttDoctorClient

    var isLaunched by mutableStateOf(false)
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var isConnected by mutableStateOf(false)
        private set
    var status by mutableStateOf("")
        private set
    var isStatusError by mutableStateOf(false)
        private set

    val patients = mutableStateListOf<Patient>()

    fun updateCredentialsProvider(userSession: CognitoUserSession) {
        val logins = HashMap<String, String>()
        logins[CognitoConfig.PROVIDER_NAME] = userSession.idToken.jwtToken
        credentialsProvider.logins = logins
    }

    private fun setStatus(message: String = "", isError: Boolean = false) {
        status = message
        isStatusError = isError
    }

    fun onLaunch() {
        isProgressing = true
        val launchJob = viewModelScope.launch(Dispatchers.IO) {
            requestObtainIdentityId()
        }
        launchJob.invokeOnCompletion { isLaunched = true }
    }

    private fun requestObtainIdentityId() {
        setStatus(OBTAIN_IDENTITY_ID_MESSAGE)
        CognitoAuthenticator.obtainIdentityId(
            credentialsProvider = credentialsProvider,
            onDone = { clientIdentityId ->
                if (clientIdentityId.isNotEmpty()) {
                    Timber.tag(DEBUG_TAG).d("identityId: $clientIdentityId")
                    this.identityId = clientIdentityId
                    mqttDoctorClient = MqttDoctorClient(
                        clientId = clientIdentityId,
                        endpoint = MqttBrokerConfig.END_POINT
                    )
                    requestCheckAttachedPolicy()
                }
            },
            onFailure = {
                val message = OBTAIN_IDENTITY_ID_FAILED + REFRESH_INSTRUCTION_POSTFIX
                setStatus(message, true)
                isProgressing = false
            }
        )
    }

    private fun requestCheckAttachedPolicy() {
        setStatus(CHECK_ATTACHED_POLICY_MESSAGE)
        MqttDoctorClient.isAttachedDoctorPolicy(
            credentialsProvider = credentialsProvider,
            identityId = identityId,
            onDone = { isCheckAttachedPolicyFailed, isAttached ->
                if (!isCheckAttachedPolicyFailed) {
                    if (isAttached) {
                        this.attachedPolicies = true
                        requestConnect()
                    }
                    else {
                        requestAttachPolicy()
                    }
                }
            },
            onFailure = {
                val message = CHECK_ATTACHED_POLICY_FAILED + REFRESH_INSTRUCTION_POSTFIX
                setStatus(message, true)
                isProgressing = false
            }
        )
    }

    private fun requestAttachPolicy() {
        setStatus(ATTACH_POLICY_MESSAGE)
        MqttDoctorClient.attachDoctorPolicy(
            credentialsProvider = credentialsProvider,
            identityId = identityId,
            onDone = { isAttachPolicyFailed ->
                if (!isAttachPolicyFailed) {
                    this.attachedPolicies = true
                    requestConnect()
                }
            },
            onFailure = {
                val message = ATTACH_POLICY_FAILED + REFRESH_INSTRUCTION_POSTFIX
                setStatus(message, true)
                isProgressing = false
            }
        )
    }

    private fun requestConnect() {
        mqttDoctorClient.connect(
            credentialsProvider = credentialsProvider,
            onStatusChange = { iotStatus ->
                when (iotStatus) {
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                        isConnected = true
                        requestResubscribe {
                            setStatus()
                            isProgressing = false
                        }
                    }
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost -> {
                        isConnected = false
                        val message = CONNECTION_LOST_MESSAGE + REFRESH_INSTRUCTION_POSTFIX
                        setStatus(message, true)
                        isProgressing = false
                    }
                    else -> {
                        isConnected = false
                        setStatus(iotStatus.name)
                        isProgressing = true
                    }
                }
            }
        )
    }


    @OptIn(ExperimentalMaterialApi::class)
    fun onSubscribeRequest(onDeviceSatisfied: () -> Unit) {
        isProgressing = true
        showDeviceIdAlreadyExistedMessage = false
        val found = patients.find { it.deviceId == deviceIdInputText }
        if (found != null) { // already existed this patient
            val showMessageJob = viewModelScope.launch {
                showDeviceIdAlreadyExistedMessage = true
                delay(MESSAGE_SHOW_TIME)
            }
            showMessageJob.invokeOnCompletion { showDeviceIdAlreadyExistedMessage = false }
            isProgressing = false
        }
        else {
            onDeviceSatisfied()
            requestSubscribe()
        }
    }

    private fun requestSubscribe() {
        patients.add(
            index = 0,
            element = Patient(
                deviceId = deviceIdInputText,
                name = nameInputText
            )
        )
        attributeInputList.forEach {  attribute ->
            if (attribute.key.isNotBlank() || attribute.value.isNotBlank()) {
                patients[0].attributes.add(attribute.clone())
            }
        }
        mqttDoctorClient.subscribe(
            deviceId = deviceIdInputText,
            onSubscriptionSuccess = {
                isProgressing = false
            },
            onSubscriptionFailure = {
                patients.removeAt(0)
                isProgressing = false
                val showErrorJob = viewModelScope.launch {
                    setStatus(SUBSCRIBE_FAILED, true)
                    delay(MESSAGE_SHOW_TIME)
                }
                showErrorJob.invokeOnCompletion { setStatus() }
            },
            onMessageCallback = this::onMessageArrive
        )
    }

    private fun onMessageArrive(topic: String, payload: ByteArray) {
        val targetDeviceId = topic.substringAfterLast('/')
        val targetPatient = patients.find { patient -> patient.deviceId == targetDeviceId }
        if (targetPatient == null) {
            Timber.tag(DEBUG_TAG).e("Could not not find patient with device<$targetDeviceId> in the patient list")
        }
        else {
            when (val topicKey = topic.substringAfter(MqttClientConfig.TOPIC_SEPARATOR).substringBefore(MqttClientConfig.TOPIC_SEPARATOR)) {
                MqttClientConfig.STATUS_TOPIC_KEY -> {
                    val statusMessage = PatientStatusMessage.create(
                        input = String(payload),
                        onFailure = { cause ->
                            Timber.tag(DEBUG_TAG).e("Failed to parse json status message: $cause")
                        }
                    )
                    if (statusMessage != null) {
                        Timber.tag(DEBUG_TAG).d("Received:\n${statusMessage.format()}")
                        if (statusMessage.cid != targetDeviceId) {
                            Timber.tag(DEBUG_TAG).w("Conflict between topic device<$targetDeviceId> and cid<${statusMessage.cid}>")
                        }
                        when (statusMessage.code) {
                            PatientStatus.Online.code -> targetPatient.status = PatientStatus.Online
                            PatientStatus.Offline.code -> targetPatient.status = PatientStatus.Offline
                            else -> Timber.tag(DEBUG_TAG).e("Unknown status code received: ${statusMessage.code}")
                        }
                    }
                }
                MqttClientConfig.DATA_TOPIC_KEY -> {
                    val dataMessage = PatientDataMessage.create(
                        input = String(payload),
                        onFailure = { cause ->
                            Timber.tag(DEBUG_TAG).e("Failed to parse json data message: $cause")
                        }
                    )
                    if (dataMessage != null) {
                        Timber.tag(DEBUG_TAG).d("Received:\n${dataMessage.format()}")
                        if (dataMessage.cid != targetDeviceId) {
                            Timber.tag(DEBUG_TAG).w("Conflict between topic device<$targetDeviceId> and cid<${dataMessage.cid}>")
                        }
                        targetPatient.updateTimestamp(dataMessage.time, UrgentApplication.systemLocale)
                        dataMessage.data.forEach { (indexKey, value) ->
                            val patientData = targetPatient.data.find { it.index.key == indexKey }
                            if (patientData == null) {
                                Timber.tag(DEBUG_TAG).e("Index key<$indexKey> did not match any predefined index key")
                            }
                            else {
                                patientData.value = value.toString()
                            }
                        }
                    }
                }
                else -> Timber.tag(DEBUG_TAG).e("Topic key<$topicKey> did not match any predefined topic key")
            }

        }
    }

    private fun requestResubscribe(onDone: () -> Unit) {
        mqttDoctorClient.resubscribe(
            devices = patients.map { it.deviceId },
            onDone = onDone,
            messageCallback = this::onMessageArrive,
            onEachFailure = { deviceId ->
                patients.removeIf { it.deviceId == deviceId }
            }
        )
    }

    var deviceIdInputText by mutableStateOf("")
        private set
    var nameInputText by mutableStateOf("")
        private set
    val attributeInputList = mutableStateListOf<PatientAttribute>()
    var showDeviceIdAlreadyExistedMessage by mutableStateOf(false)
        private set
    var attributeWarning by mutableStateOf("")
        private set
    fun onDeviceIdInputTextChange(text: String) {
        deviceIdInputText = text
    }
    fun onNameInputTextChange(text: String) {
        nameInputText = text
    }
    fun onAddNewAttribute(exceedingMessage: String) {
        if (attributeInputList.size >= Patient.MAXIMUM_ATTRIBUTES_ALLOWED) {
            showAttributeEditWarning(exceedingMessage)
        }
        else {
            attributeInputList.add(
                element = PatientAttribute(
                    key = "",
                    value = "",
                    pinned = attributeInputList.size < Patient.MAXIMUM_PINNED_ATTRIBUTES_ALLOWED
                )
            )
        }
    }
    fun onAttributePinStateChange(attribute: PatientAttribute, exceedingMessage: String) {
        when(attribute.pinned) {
            true -> attribute.pinned = false
            false -> {
                if (attributeInputList.count { it.pinned } < Patient.MAXIMUM_PINNED_ATTRIBUTES_ALLOWED ) {
                    attribute.pinned = true
                }
                else {
                    showAttributeEditWarning(exceedingMessage)
                }
            }
        }
    }
    private fun showAttributeEditWarning(message: String) {
        val showWarningJob = viewModelScope.launch {
            attributeWarning = message
            delay(MESSAGE_SHOW_TIME)
        }
        showWarningJob.invokeOnCompletion { attributeWarning = "" }
    }
    fun isDeviceIdInputTextError() = deviceIdInputText.isNotEmpty() && !InfoValidator.isDeviceIdValid(deviceIdInputText)
    fun isSubscribeButtonEnable() = InfoValidator.isDeviceIdValid(deviceIdInputText) && !isProgressing && isConnected

    fun onDeletePatientRequest(patient: Patient, onDone: () -> Unit) {
        mqttDoctorClient.unsubscribe(patient.deviceId)
        patients.remove(patient)
        onDone()
    }

    private var identityId = ""
    private var attachedPolicies = false
    var isRefreshing by mutableStateOf(false)
        private set
    fun onRefreshRequest() {
        val showRefreshJob = viewModelScope.launch {
            isRefreshing = true
            delay(1000L)
            isRefreshing = false
        }
        showRefreshJob.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.IO) {
                isProgressing = true
                requestRefresh()
            }
        }
    }

    private fun requestRefresh() {
        if (identityId.isBlank()) {
            requestObtainIdentityId()
        }
        else if (!attachedPolicies) {
            requestAttachPolicy()
        }
        else if (!isConnected) {
            requestConnect()
        }
        else {
            isProgressing = false
        }
    }

    companion object {
        private const val DEBUG_TAG = "mqtt"
        private const val OBTAIN_IDENTITY_ID_MESSAGE = "Obtaining identity id"
        private const val CHECK_ATTACHED_POLICY_MESSAGE = "Checking attached policies"
        private const val ATTACH_POLICY_MESSAGE = "Attaching required policies"
        private const val CONNECTION_LOST_MESSAGE = "Connection lost."

        private const val OBTAIN_IDENTITY_ID_FAILED = "Failed to obtain identity id."
        private const val CHECK_ATTACHED_POLICY_FAILED = "Failed to check attached policies."
        private const val ATTACH_POLICY_FAILED = "Failed to attach policy."
        private const val SUBSCRIBE_FAILED = "Failed to subscribe. Please try again."
        private const val REFRESH_INSTRUCTION_POSTFIX = " Swipe down to refresh."

        private const val MESSAGE_SHOW_TIME = 3000L  // in ms
    }

    override fun onCleared() {
        super.onCleared()
        if (this::mqttDoctorClient.isInitialized) {
            mqttDoctorClient.disconnect()
        }
    }
}