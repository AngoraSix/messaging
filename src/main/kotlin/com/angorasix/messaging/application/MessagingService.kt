package com.angorasix.messaging.application

import com.angorasix.commons.infrastructure.intercommunication.dto.invitations.A6InfraClubInvitation
import com.angorasix.messaging.infrastructure.config.configurationproperty.a6infra.A6InfraConfigurations
import com.angorasix.messaging.infrastructure.dto.CustomClubInvitationEmailContent
import com.angorasix.messaging.infrastructure.dto.ProjectClubInvitationEmailContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*

/**
 *
 *
 * @author rozagerardo
 */
class MessagingService(
    private val infraConfigs: A6InfraConfigurations,
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    private val messageSource: MessageSource,
) {
    /* default */
    private val logger: Logger = LoggerFactory.getLogger(MessagingService::class.java)

    suspend fun processContributorInvitation(invitation: A6InfraClubInvitation): Flow<Boolean> =
        flow {
            logger.debug("Processing contributor invitation: {}", invitation)
            val tokenUrl = infraConfigs.patternUrls.invitationUrlPattern
                .replace(infraConfigs.patternPlaceholders.clubId, invitation.club.id)
                .replace(infraConfigs.patternPlaceholders.invitationToken, invitation.token)
            val projectId = invitation.club.projectId
            if (projectId != null) {
                val emailContent = ProjectClubInvitationEmailContent(
                    to = invitation.email,
                    subject = "AngoraSix Notification",
                    projectUrl = infraConfigs.patternUrls.projectUrlPattern
                        .replace(
                            infraConfigs.patternPlaceholders.projectId,
                            projectId,
                        ),
                    invitationUrl = tokenUrl,
                    locale = Locale.of("es"),
                )
                sendInvitationTemplatedMail(emailContent, projectId)
            } else {
                val emailContent = CustomClubInvitationEmailContent(
                    to = invitation.email,
                    subject = "AngoraSix Notification",
                    clubName = invitation.club.name,
                    invitationUrl = tokenUrl,
                    locale = Locale.of("es"),
                )
                sendInvitationTemplatedMail(emailContent)
            }
            emit(true)
        }

    private fun sendInvitationTemplatedMail(
        content: ProjectClubInvitationEmailContent,
        projectId: String,
    ) {
        try {
            // 1. Build the dynamic data for the template
            val context = Context(content.locale).apply {
                setVariable("projectUrl", content.projectUrl)
                setVariable("invitationUrl", content.invitationUrl)
            }
            // 2. Process the Thymeleaf template
            val htmlContent = templateEngine.process("invitation.email.project", context)

            // 3. Build a MimeMessage
            val mimeMessage = mailSender.createMimeMessage()
            // True = multipart
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
            helper.setFrom(infraConfigs.mailingConfigs.fromEmail, infraConfigs.mailingConfigs.fromName)
            helper.setTo(content.to)
            val subject = messageSource.getMessage(
                "invite.project.subject", // the key in messages.properties
                arrayOf(),      // placeholders
                content.locale, // the user's Locale
            )
            helper.setSubject(subject)
            // "true" indicates HTML content
            helper.setText(htmlContent, true)

            // 4. Send the email
            mailSender.send(mimeMessage)
            logger.debug("HTML Invitation email sent to ${content.to} for project $projectId")
        } catch (ex: Exception) {
            logger.error("Error sending HTML invitation email to ${content.to}", ex)
            throw ex
        }
    }

    private fun sendInvitationTemplatedMail(content: CustomClubInvitationEmailContent) {
        try {
            // 1. Build the dynamic data for the template
            val context = Context(content.locale).apply {
                setVariable("clubName", content.clubName)
                setVariable("invitationUrl", content.invitationUrl)
            }
            // 2. Process the Thymeleaf template
            val htmlContent = templateEngine.process("invitation.email.custom", context)

            // 3. Build a MimeMessage
            val mimeMessage = mailSender.createMimeMessage()
            // True = multipart
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
            helper.setTo(content.to)
            val subject = messageSource.getMessage(
                "invite.custom.subject",       // the key in messages.properties
                arrayOf(content.clubName),      // placeholders {0} replaced with content.clubName
                content.locale,                  // the user's Locale
            )
            helper.setSubject(subject)
            // "true" indicates HTML content
            helper.setText(htmlContent, true)

            // 4. Send the email
            mailSender.send(mimeMessage)
            logger.debug("HTML Invitation email sent to ${content.to} for club ${content.clubName}")
        } catch (ex: Exception) {
            logger.error("Error sending HTML invitation email to ${content.to}", ex)
            throw ex
        }
    }
}
