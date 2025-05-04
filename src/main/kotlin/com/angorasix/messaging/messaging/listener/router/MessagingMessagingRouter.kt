package com.angorasix.messaging.messaging.listener.router

import com.angorasix.commons.infrastructure.intercommunication.club.UserInvited
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.messaging.messaging.listener.handler.MessagingMessagingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * <p>
 *  NOTE: spring-cloud-streams is not prepared to handle Kotlin DSL beans:
 *  https://github.com/spring-cloud/spring-cloud-stream/issues/2025
 * </p>
 *
 * @author rozagerardo
 */
@Configuration
class MessagingMessagingRouter(
    val handler: MessagingMessagingHandler,
) {
    @Bean
    fun clubInvitation(): (A6InfraMessageDto<UserInvited>) -> Unit = { handler.clubContributorInvitation(it) }
}
