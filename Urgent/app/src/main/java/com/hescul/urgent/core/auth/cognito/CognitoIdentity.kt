package com.hescul.urgent.core.auth.cognito

import com.amazonaws.regions.Regions

data class CognitoIdentity(
    val poolId: String,
    val clientId: String,
    val clientSecret: String,
    val region: Regions,
)
