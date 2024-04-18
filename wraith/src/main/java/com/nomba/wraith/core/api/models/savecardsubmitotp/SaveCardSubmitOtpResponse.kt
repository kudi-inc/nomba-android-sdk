package com.nomba.wraith.core.api.models.savecardsubmitotp

data class SaveCardSubmitOtpResponse(
    val code: String,
    val `data`: Data,
    val description: String
)