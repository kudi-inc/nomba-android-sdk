package com.nomba.wraith.core.api.models.submitcard

data class Data(
    val message: String,
    val responseCode: String,
    val secureAuthenticationData: SecureAuthenticationData,
    val status: String,
    val transactionId: String
)