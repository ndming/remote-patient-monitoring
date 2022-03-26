package com.hescul.urgent.core.auth.cognito

import com.amazonaws.regions.Regions
import com.amazonaws.util.VersionInfoUtils

object CognitoConfig  {
    const val DEBUG_TAG = "authCognito"

    const val POOL_ID = "us-east-1_JsBf49YSf"
    const val CLIENT_ID = "6beddanofjgqq8f8nb2o386ad6"
    const val CLIENT_SECRET = "11ih4or60jidu22m9a89oil75r0rekf1a74rf4ocaai9nlhqvj90"
    val REGION = Regions.US_EAST_1

    const val DEFAULT_CONNECTION_TIMEOUT = 15 * 1000
    const val DEFAULT_SOCKET_TIMEOUT = 15 * 1000
    const val DEFAULT_MAX_CONNECTIONS = 10
    val DEFAULT_USER_AGENT: String = VersionInfoUtils.getUserAgent()
}

