package com.nomba.wraith.core.api.models.savecard

data class SaveCardOtpResponse(
    val code: String,
    val `data`: Data,
    val description: String
)