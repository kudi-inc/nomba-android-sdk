package com.nomba.wraith.core.api.models.accesstoken

data class AccessTokenResponse(
    val code: String,
    val `data`: Data,
    val description: String
)