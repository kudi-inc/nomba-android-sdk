package com.nomba.wraith.core.api.models.accesstoken

data class AccessTokenRequest(
    val client_id: String,
    val client_secret: String,
    val grant_type: String
)