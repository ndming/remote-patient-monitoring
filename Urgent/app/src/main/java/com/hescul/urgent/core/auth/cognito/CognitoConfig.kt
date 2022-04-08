package com.hescul.urgent.core.auth.cognito

import com.amazonaws.regions.Regions
import com.amazonaws.util.VersionInfoUtils

/**
 * This object defines required information for AWS Cognito usage.
 */
object CognitoConfig  {
    const val POOL_ID = "us-east-1_JsBf49YSf"
    const val CLIENT_ID = "6beddanofjgqq8f8nb2o386ad6"
    const val CLIENT_SECRET = "11ih4or60jidu22m9a89oil75r0rekf1a74rf4ocaai9nlhqvj90"
    val REGION = Regions.US_EAST_1

    const val IDENTITY_POOL_ID = "us-east-1:e1bf1dff-d174-49e2-91ab-4276e9a171c5"
    const val PROVIDER_NAME = "cognito-idp.us-east-1.amazonaws.com/us-east-1_JsBf49YSf"
}

