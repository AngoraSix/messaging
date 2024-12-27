package com.angorasix.messaging.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.dto.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.dto.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.dto.invitations.A6InfraClubInvitation
import com.angorasix.commons.infrastructure.intercommunication.dto.messaging.A6InfraMessageDto
import com.angorasix.messaging.application.MessagingService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking

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
    fun clubContributorInvitation(message: A6InfraMessageDto) = runBlocking {
        if (message.topic == A6InfraTopics.CLUB_INVITATION.value &&
            message.targetType == A6DomainResource.Contributor &&
            message.objectType == A6DomainResource.Club.value
        ) {
            val invitationDto = message.extractInfraClubInvitation(objectMapper)
            messagingService.processContributorInvitation(invitationDto)
        }
    }
}

private fun A6InfraMessageDto.extractInfraClubInvitation(
    objectMapper: ObjectMapper,
): A6InfraClubInvitation {
    val invitationDto = objectMapper.convertValue(messageData, A6InfraClubInvitation::class.java)
    return invitationDto
}
