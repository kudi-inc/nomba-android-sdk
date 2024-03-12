package com.nomba.wraith.core.api.models.submitotp

data class SubmitOTPRequest(
    val orderReference: String,
    val otp: String,
    val transactionId: String
)