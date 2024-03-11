package com.nomba.wraith.core.api.models.createorder

data class Order(
    val amount: String,
    val callbackUrl: String,
    val currency: String,
    val customerEmail: String,
    val customerId: String,
    val orderReference: String
)