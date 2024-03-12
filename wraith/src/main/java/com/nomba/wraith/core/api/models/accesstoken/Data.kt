package com.nomba.wraith.core.api.models.accesstoken

data class Data(
    val access_token: String,
    val businessId: String,
    val expiresAt: String,
    val refresh_token: String
)