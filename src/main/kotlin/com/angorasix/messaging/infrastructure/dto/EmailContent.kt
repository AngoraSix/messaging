package com.angorasix.messaging.infrastructure.dto

import java.util.*

open class EmailContent(
    open val to: String,
    open val subject: String,
    open val locale: Locale,
    open val body: String? = null,
)

data class ProjectClubInvitationEmailContent(
    override val to: String,
    override val subject: String,
    override val locale: Locale,
    val projectUrl: String,
    val invitationUrl: String,
) : EmailContent(to, subject, locale)

data class CustomClubInvitationEmailContent(
    override val to: String,
    override val subject: String,
    override val locale: Locale,
    val clubName: String,
    val invitationUrl: String,
) : EmailContent(to, subject, locale)