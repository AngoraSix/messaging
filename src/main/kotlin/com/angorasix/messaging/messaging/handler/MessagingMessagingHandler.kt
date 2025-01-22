package com.angorasix.messaging.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.dto.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.dto.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.dto.invitations.A6InfraClubInvitation
import com.angorasix.commons.infrastructure.intercommunication.dto.messaging.A6InfraMessageDto
import com.angorasix.messaging.application.MessagingService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class MessagingMessagingHandler(
    private val messagingService: MessagingService,
    private val objectMapper: ObjectMapper,
) {

    private val emailRegex = Regex(EMAIL_REGEX)
    /* default */
    private val logger: Logger = LoggerFactory.getLogger(MessagingMessagingHandler::class.java)

    fun clubContributorInvitation(message: A6InfraMessageDto) = runBlocking {
        if (message.topic == A6InfraTopics.CLUB_INVITATION.value &&
            message.targetType == A6DomainResource.Contributor &&
            message.objectType == A6DomainResource.Club.value
        ) {
            val invitationDto = message.extractInfraClubInvitation(objectMapper)
            if(!emailRegex.matches(invitationDto.email)){
                logger.error("Invalid email format for invitation: {}", invitationDto.toString())
            } else {
                messagingService.processContributorInvitation(invitationDto).collect { result ->
                    // Now you can do something with 'result'
                    if (!result) {
                        logger.error("Error processing invitation: {}", invitationDto.toString())
                    }
                }
            }

        }
    }

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    }
}

private fun A6InfraMessageDto.extractInfraClubInvitation(
    objectMapper: ObjectMapper,
): A6InfraClubInvitation {
    val invitationDto = objectMapper.convertValue(messageData, A6InfraClubInvitation::class.java)
    return invitationDto
}
