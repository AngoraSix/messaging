package com.angorasix.messaging.application

import com.angorasix.commons.infrastructure.intercommunication.club.UserInvited
import com.angorasix.commons.infrastructure.intercommunication.survey.SurveyRegistered
import com.angorasix.messaging.infrastructure.config.a6infra.A6InfraConfigurations
import com.angorasix.messaging.infrastructure.dto.CustomClubInvitationEmailContent
import com.angorasix.messaging.infrastructure.dto.ProjectClubInvitationEmailContent
import com.angorasix.messaging.infrastructure.dto.SurveyRegisteredEmailContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

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
    // default
    private val logger: Logger = LoggerFactory.getLogger(MessagingService::class.java)

    fun processContributorInvitation(invitation: UserInvited): Flow<Boolean> =
        flow {
            logger.debug("Processing contributor invitation: {}", invitation)
            val tokenUrl =
                infraConfigs.patternUrls.invitationUrlPattern
                    .replace(infraConfigs.patternPlaceholders.clubId, invitation.club.id)
                    .replace(infraConfigs.patternPlaceholders.invitationToken, invitation.token)
            val projectManagementId = invitation.club.managementId
            if (projectManagementId != null) {
                val emailContent =
                    ProjectClubInvitationEmailContent(
                        to = invitation.email,
                        subject = "AngoraSix Notification",
                        projectUrl =
                            infraConfigs.patternUrls.projectUrlPattern
                                .replace(
                                    infraConfigs.patternPlaceholders.projectManagementId,
                                    projectManagementId,
                                ),
                        invitationUrl = tokenUrl,
                        locale = Locale.of("en"),
                    )
                sendInvitationTemplatedMail(emailContent, projectManagementId)
            } else {
                val emailContent =
                    CustomClubInvitationEmailContent(
                        to = invitation.email,
                        subject = "AngoraSix Notification",
                        clubName = invitation.club.name,
                        invitationUrl = tokenUrl,
                        locale = Locale.of("en"),
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
            val context =
                Context(content.locale).apply {
                    setVariable("projectUrl", content.projectUrl)
                    setVariable("invitationUrl", content.invitationUrl)
                }
            // 2. Process the Thymeleaf template
            val htmlContent = templateEngine.process("invitation.email.project", context)

            // 3. Build a MimeMessage
            val mimeMessage = mailSender.createMimeMessage()
            // True = multipart
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
            helper.setFrom(
                infraConfigs.mailingConfigs.fromEmail,
                infraConfigs.mailingConfigs.fromName,
            )
            helper.setTo(content.to)
            val subject =
                messageSource.getMessage(
                    "invite.project.subject", // the key in messages.properties
                    arrayOf(), // placeholders
                    content.locale, // the user's Locale
                )
            helper.setSubject(subject)
            // "true" indicates HTML content
            helper.setText(htmlContent, true)

            // 4. Send the email
            mailSender.send(mimeMessage)
            logger.debug("HTML Invitation email sent to ${content.to} for project $projectId")
        } catch (ex: MailException) {
            logger.error("Error sending invitation email to ${content.to}", ex)
            throw ex
        }
    }

    private fun sendInvitationTemplatedMail(content: CustomClubInvitationEmailContent) {
        try {
            // 1. Build the dynamic data for the template
            val context =
                Context(content.locale).apply {
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
            val subject =
                messageSource.getMessage(
                    "invite.custom.subject", // the key in messages.properties
                    arrayOf(content.clubName), // placeholders {0} replaced with content.clubName
                    content.locale, // the user's Locale
                )
            helper.setSubject(subject)
            // "true" indicates HTML content
            helper.setText(htmlContent, true)

            // 4. Send the email
            mailSender.send(mimeMessage)
            logger.debug("HTML Invitation email sent to ${content.to} for club ${content.clubName}")
        } catch (ex: MailException) {
            logger.error("Error sending invitation email to ${content.to}", ex)
            throw ex
        }
    }

    fun processSurveyRegistered(surveyRegistered: SurveyRegistered): Flow<Boolean> =
        flow {
            logger.debug("Processing survey registered: {}", surveyRegistered)
            val emailContent =
                SurveyRegisteredEmailContent(
                    to = infraConfigs.mailingConfigs.platformAdminEmail,
                    subject = "Survey Registered: %s".format(surveyRegistered.surveyKey),
                    surveyId = surveyRegistered.surveyResponseId,
                    responsesString = surveyRegistered.response.toString(),
                    surveyKey = surveyRegistered.surveyKey,
                    locale = Locale.of("en"),
                )
            sendSurveyRegisteredTemplatedMail(emailContent)
            emit(true)
        }

    private fun sendSurveyRegisteredTemplatedMail(content: SurveyRegisteredEmailContent) {
        try {
            // 1. Build the dynamic data for the template
            val context =
                Context(content.locale).apply {
                    setVariable("contributorId", content.contributorId ?: "unknown")
                    setVariable("surveyId", content.surveyId)
                    setVariable("surveyKey", content.surveyKey)
                    setVariable("responsesString", content.responsesString)
                }
            // 2. Process the Thymeleaf template
            val htmlContent = templateEngine.process("surveysregistered.email.platformadmin", context)

            // 3. Build a MimeMessage
            val mimeMessage = mailSender.createMimeMessage()
            // True = multipart
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
            helper.setFrom(
                infraConfigs.mailingConfigs.fromEmail,
                infraConfigs.mailingConfigs.fromName,
            )
            helper.setTo(content.to)
            val subject =
                messageSource.getMessage(
                    "survey.registered.subject", // the key in messages.properties
                    arrayOf(content.surveyKey), // placeholders
                    content.locale, // the user's Locale
                )
            helper.setSubject(subject)
            // "true" indicates HTML content
            helper.setText(htmlContent, true)

            // 4. Send the email
            mailSender.send(mimeMessage)
            logger.debug("HTML Survey Registered email sent to ${content.to} for surveyId ${content.surveyId}")
        } catch (ex: MailException) {
            logger.error("Error sending Survey Registered email to ${content.to}", ex)
            throw ex
        }
    }
}
