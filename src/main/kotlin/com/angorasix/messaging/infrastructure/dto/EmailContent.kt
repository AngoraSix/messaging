package com.angorasix.messaging.infrastructure.dto

data class EmailContent(
    val to: String,
    val subject: String,
    val body: String,
)