package com.nomba.wraith.core.api.models.transationstatus

data class CheckTransactionStatusResponse(
    val code: String,
    val `data`: Data,
    val description: String
)