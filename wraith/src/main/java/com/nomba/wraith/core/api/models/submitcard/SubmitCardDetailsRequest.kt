package com.nomba.wraith.core.api.models.submitcard

data class SubmitCardDetailsRequest(
    val cardDetails: String,
    val deviceInformation: DeviceInformation,
    val key: String,
    val orderReference: String,
    val saveCard: String
)