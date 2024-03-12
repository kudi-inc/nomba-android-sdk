package com.nomba.wraith.core.api.models.submitotp

data class SubmitOTPResponse(
    val code: String,
    val `data`: Data,
    val description: String
)