package com.hescul.urgent.core.mqtt

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.*
import com.amazonaws.regions.Region
import com.amazonaws.services.iot.AWSIotClient
import com.amazonaws.services.iot.model.AttachPolicyRequest
import com.amazonaws.services.iot.model.ListAttachedPoliciesRequest
import org.json.JSONObject
import timber.log.Timber

class MqttDoctorClient(clientId: String, endpoint: String) {
    private val mqttManager = AWSIotMqttManager(clientId, endpoint)
    init {
        mqttManager.maxAutoReconnectAttempts = MqttClientConfig.MAX_AUTO_RECONNECTION_ATTEMPTS
        mqttManager.setCleanSession(MqttClientConfig.CLEAN_SESSION)
    }

    companion object {
        private const val DEBUG_TAG = "mqtt"
        private const val DEFAULT_SUBSCRIPTION_FAIL_CAUSE = "Subscription failed"
        private const val DEFAULT_REQUEST_POLICY_LIST_FAIL_CAUSE = "Failed to request policy list"
        private const val DEFAULT_ATTACH_DOCTOR_POLICY_FAIL_CAUSE = "Failed to attach policy list"

        fun isAttachedDoctorPolicy(
            credentialsProvider: CognitoCachingCredentialsProvider,
            identityId: String,
            onDone: (Boolean, Boolean) -> Unit,
            onFailure: (String) -> Unit
        ) {
            val listAttachedPoliciesRequest = ListAttachedPoliciesRequest()
            listAttachedPoliciesRequest.target = identityId
            var isFailed = false
            val isAttached = try {
                val result = AWSIotClient(credentialsProvider).listAttachedPolicies(listAttachedPoliciesRequest)
                result.policies.find { policy -> policy.policyName == MqttBrokerConfig.DOCTOR_POLICY } != null
            } catch (exception: AmazonServiceException) {
                Timber.tag(DEBUG_TAG).e("list attached policies failed<service>: ${exception.errorMessage}")
                onFailure(exception.errorCode)
                isFailed = true
                false
            } catch (exception: AmazonClientException) {
                Timber.tag(DEBUG_TAG).e("list attached policies failed<client>: ${exception.message}")
                onFailure(exception.message ?: DEFAULT_REQUEST_POLICY_LIST_FAIL_CAUSE)
                isFailed = true
                false
            }
            Timber.tag(DEBUG_TAG).d("Is user<$identityId> attached <${MqttBrokerConfig.DOCTOR_POLICY}>: $isAttached")
            onDone(isFailed, isAttached)
        }

        fun attachDoctorPolicy(
            credentialsProvider: CognitoCachingCredentialsProvider,
            identityId: String,
            onDone: (Boolean) -> Unit,
            onFailure: (String) -> Unit
        ) {
            val attachedPoliciesRequest = AttachPolicyRequest()
            attachedPoliciesRequest.policyName = MqttBrokerConfig.DOCTOR_POLICY
            attachedPoliciesRequest.target = identityId
            val iotClient = AWSIotClient(credentialsProvider)
            iotClient.setRegion(Region.getRegion(MqttBrokerConfig.REGION))
            var isFailed = false
            try {
                iotClient.attachPolicy(attachedPoliciesRequest)
            } catch (exception: AmazonServiceException) {
                Timber.tag(DEBUG_TAG).e("attach policies failed: ${exception.errorMessage}")
                onFailure(exception.errorCode)
                isFailed = true
            } catch (exception: AmazonClientException) {
                Timber.tag(DEBUG_TAG).e("attach policies failed: ${exception.message}")
                onFailure(exception.message ?: DEFAULT_ATTACH_DOCTOR_POLICY_FAIL_CAUSE)
                isFailed = true
            }
            onDone(isFailed)
        }
    }

    fun connect(
        credentialsProvider: CognitoCachingCredentialsProvider,
        onStatusChange: (AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus) -> Unit,
    ) {
        val statusCallback = AWSIotMqttClientStatusCallback { status, _ ->
            Timber.tag(DEBUG_TAG).d("status<${status.name}>")
            onStatusChange(status)
        }
        mqttManager.connect(credentialsProvider, statusCallback)
    }

    fun disconnect(): Boolean = mqttManager.disconnect()

    /**
     * Subscribe to the device with the id [deviceId]
     */
    fun subscribe(
        deviceId: String,
        onSubscriptionSuccess: () -> Unit,
        onSubscriptionFailure: (String) -> Unit,
        onDataMessageCallback: (String, JSONObject) -> Unit,
        onStatusMessageCallback: (String, Int) -> Unit,
        defaultFailCause: String = DEFAULT_SUBSCRIPTION_FAIL_CAUSE,
    ) {
        val dataTopic = MqttClientConfig.DATA_TOPIC_PREFIX + deviceId
        val statusTopic = MqttClientConfig.STATUS_TOPIC_PREFIX + deviceId

        val subscriptionCallback = object: AWSIotMqttSubscriptionStatusCallback {
            override fun onSuccess() {
                Timber.tag(DEBUG_TAG).d("subscribed successfully!")
                onSubscriptionSuccess()
            }

            override fun onFailure(exception: Throwable?) {
                Timber.tag(DEBUG_TAG).e("subscription failed: ${exception?.message}")
                val failCause = exception?.message ?: defaultFailCause
                onSubscriptionFailure(failCause)
            }
        }
        val messageCallback = AWSIotMqttNewMessageCallback { topic, data ->
            val js = JSONObject(String(data))
            Timber.tag(DEBUG_TAG).d("message received: topic<$topic> | data:\n${js.toString(2)}")


        }

    }
}