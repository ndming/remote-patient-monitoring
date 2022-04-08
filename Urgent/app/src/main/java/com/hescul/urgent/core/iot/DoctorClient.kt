package com.hescul.urgent.core.iot

import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.*
import timber.log.Timber

class DoctorClient(
    clientId: String,
    endpoint: String,
) {
    private val mqttManager = AWSIotMqttManager(clientId, endpoint)

    companion object {
        private const val DEBUG_TAG = "mqtt"
        private const val DEFAULT_SUBSCRIPTION_FAIL_CAUSE = "Subscription failed"
    }

    fun connect(
        credentialsProvider: CognitoCachingCredentialsProvider,
        onStatusChange: (AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus) -> Unit,
    ) {
        val statusCallback = AWSIotMqttClientStatusCallback { status, _ ->
                Timber.tag(DEBUG_TAG).d("connection: status<${status.name}>")
                onStatusChange(status)
            }
        mqttManager.connect(credentialsProvider, statusCallback)
    }

    fun subscribe(
        subscribeTopic: String,
        subscribeQos: AWSIotMqttQos,
        onSubscriptionSuccess: () -> Unit,
        onSubscriptionFailure: (String) -> Unit,
        onMessageCallback: (String, ByteArray) -> Unit,
        defaultFailCause: String = DEFAULT_SUBSCRIPTION_FAIL_CAUSE,
    ) {
        val subscriptionCallback = object: AWSIotMqttSubscriptionStatusCallback {
            override fun onSuccess() {
                Timber.tag(DEBUG_TAG).d("subscribed succeeded!")
                onSubscriptionSuccess()
            }

            override fun onFailure(exception: Throwable?) {
                Timber.tag(DEBUG_TAG).e("subscription failed: ${exception?.message}")
                val failCause = exception?.message ?: defaultFailCause
                onSubscriptionFailure(failCause)
            }
        }
        val messageCallback = AWSIotMqttNewMessageCallback { topic, data ->
                Timber.tag(DEBUG_TAG).d("message received: topic<${topic}> | data<${data}>")
                onMessageCallback(topic, data)
            }
        mqttManager.subscribeToTopic(subscribeTopic, subscribeQos, subscriptionCallback, messageCallback)
    }
}