package com.hescul.urgent.core.mqtt

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos

@Suppress("SpellCheckingInspection")
object MqttBrokerConfig {
    const val END_POINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"
    const val DOCTOR_POLICY = "RPMDOC_policy"
    const val REGION = "us-east-1"
}

object MqttClientConfig {
    const val DATA_TOPIC_KEY = "sos"
    const val STATUS_TOPIC_KEY = "tus"
    const val DOCTOR_TOPIC_KEY = "doc"
    const val TOPIC_SEPARATOR = '/'
    const val DATA_TOPIC_PREFIX = "rpm$TOPIC_SEPARATOR$DATA_TOPIC_KEY$TOPIC_SEPARATOR"
    const val STATUS_TOPIC_PREFIX = "rpm$TOPIC_SEPARATOR$STATUS_TOPIC_KEY$TOPIC_SEPARATOR"
    const val DOCTOR_TOPIC_PREFIX = "rpm$TOPIC_SEPARATOR$DOCTOR_TOPIC_KEY$TOPIC_SEPARATOR"

    val SUBSCRIBE_QOS = AWSIotMqttQos.QOS1

    const val MAX_AUTO_RECONNECTION_ATTEMPTS = 2
    const val CLEAN_SESSION = true
    const val AUTO_RESUBSCRIBE = false
}