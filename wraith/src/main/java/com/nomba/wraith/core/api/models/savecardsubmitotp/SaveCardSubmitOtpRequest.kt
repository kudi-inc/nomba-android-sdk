package com.nomba.wraith.core.api.models.savecardsubmitotp

data class SaveCardSubmitOtpRequest(
    val orderReference: String,
    val otp: String,
    val phoneNumber: String
)