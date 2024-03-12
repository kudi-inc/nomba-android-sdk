package com.nomba.wraith.core.api.models.createorder

data class CreateOrderRequest(
    val order: Order,
    val tokenizeCard: String
)