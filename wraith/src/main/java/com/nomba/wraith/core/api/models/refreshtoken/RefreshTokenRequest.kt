package com.nomba.wraith.core.api.models.refreshtoken

data class RefreshTokenRequest(
    val grant_type: String,
    val refresh_token: String
)