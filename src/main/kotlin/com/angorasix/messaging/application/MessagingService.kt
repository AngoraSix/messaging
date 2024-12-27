package com.angorasix.messaging.application

import com.angorasix.commons.infrastructure.intercommunication.dto.invitations.A6InfraClubInvitation
import com.angorasix.messaging.infrastructure.config.configurationproperty.a6infra.A6InfraConfigurations
import com.angorasix.messaging.infrastructure.dto.EmailContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

/**
 *
 *
 * @author rozagerardo
 */
class MessagingService(
    private val infraConfigs: A6InfraConfigurations,
    private val mailSender: JavaMailSender
) {
    suspend fun processContributorInvitation(invitation: A6InfraClubInvitation): Flow<Boolean> = flow {
        val tokenUrl = infraConfigs.patternUrls.invitationUrlPattern
            .replace(infraConfigs.patternPlaceholders.clubId, invitation.club.id)
            .replace(infraConfigs.patternPlaceholders.invitationToken, invitation.token)
        println("Generated token URL: $tokenUrl")
        val emailContent = EmailContent(
            to = invitation.email,
            subject = "AngoraSix Notification",
            body = "You have been invited to a Club: $tokenUrl"
        )
        sendSimpleMail(emailContent)
        emit(true)
        // send email
    }

    private fun sendSimpleMail(content: EmailContent) {
        try {
            val mailMessage = SimpleMailMessage()
            mailMessage.setTo(content.to)
            mailMessage.subject = content.subject
            mailMessage.text = content.body
            mailSender.send(mailMessage)
            println("Email sent successfully to ${content.to}")
        } catch (ex: Exception) {
            println("Error sending email to ${content.to}: $ex")
            throw ex
        }
    }
}
