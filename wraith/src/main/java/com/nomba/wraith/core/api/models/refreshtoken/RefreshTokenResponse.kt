package com.nomba.wraith.core.api.models.refreshtoken

data class RefreshTokenResponse(
    val code: String,
    val `data`: Data,
    val description: String
)