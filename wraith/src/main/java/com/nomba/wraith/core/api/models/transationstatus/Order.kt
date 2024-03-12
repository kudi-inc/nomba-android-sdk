package com.nomba.wraith.core.api.models.transationstatus

data class Order(
    val accountId: String,
    val amount: String,
    val businessEmail: String,
    val businessLogo: String,
    val businessName: String,
    val callbackUrl: String,
    val currency: String,
    val customerEmail: String,
    val customerId: String,
    val orderId: String,
    val orderReference: String
)