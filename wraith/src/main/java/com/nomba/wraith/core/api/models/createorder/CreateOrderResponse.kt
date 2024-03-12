package com.nomba.wraith.core.api.models.createorder

data class CreateOrderResponse(
    val code: String,
    val `data`: Data,
    val description: String
)