package com.nomba.wraith.core.api.models.fetchparentaccount

data class Data(
    val accountHolderId: String,
    val accountId: String,
    val accountName: String,
    val accountRef: String,
    val banks: List<Bank>,
    val bvn: String,
    val createdAt: String,
    val currency: String,
    val status: String,
    val type: String
)