package com.nomba.wraith.core.api.models.savecard

data class SaveCardOtpRequest(
    val orderReference: String,
    val phoneNumber: String
)