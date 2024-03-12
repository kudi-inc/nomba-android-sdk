package com.nomba.wraith.core.api.models.submitcard

data class SecureAuthenticationData(
    val acsUrl: String,
    val jwt: String,
    val md: String,
    val termUrl: String
)