package com.angorasix.messaging.messaging.listener.handler

import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.club.UserInvited
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.messaging.application.MessagingService
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
) {
    private val emailRegex = Regex(EMAIL_REGEX)

    // default
    private val logger: Logger = LoggerFactory.getLogger(MessagingMessagingHandler::class.java)

    fun clubContributorInvitation(message: A6InfraMessageDto<UserInvited>) =
        runBlocking {
            if (message.topic == A6InfraTopics.CLUB_INVITATION.value &&
                message.targetType == A6DomainResource.CONTRIBUTOR &&
                message.objectType == A6DomainResource.CLUB.value
            ) {
                val userInvited = message.messageData
                if (!emailRegex.matches(userInvited.email)) {
                    logger.error("Invalid email format for invitation: {}", userInvited.toString())
                } else {
                    messagingService.processContributorInvitation(userInvited).collect { result ->
                        // Now you can do something with 'result'
                        if (!result) {
                            logger.error("Error processing invitation: {}", userInvited.toString())
                        }
                    }
                }
            }
        }

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    }
}
