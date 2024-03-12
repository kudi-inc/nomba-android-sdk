package com.nomba.wraith.core.api.models.submitcard

data class DeviceInformation(
    val deviceChannel: String,
    val httpBrowserColorDepth: String,
    val httpBrowserJavaEnabled: String,
    val httpBrowserJavaScriptEnabled: String,
    val httpBrowserLanguage: String,
    val httpBrowserScreenHeight: String,
    val httpBrowserScreenWidth: String,
    val httpBrowserTimeDifference: String,
    val userAgentBrowserValue: String
)