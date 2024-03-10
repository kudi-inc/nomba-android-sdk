package com.nomba.wraith.core.api.models.refreshtoken

data class Data(
    val access_token: String,
    val businessId: String,
    val expiresAt: String,
    val refresh_token: String
)