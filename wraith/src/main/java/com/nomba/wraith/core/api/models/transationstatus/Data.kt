package com.nomba.wraith.core.api.models.transationstatus

data class Data(
    val message: String,
    val order: Order,
    val status: String
)