package com.angorasix.messaging.messaging.router

import com.angorasix.commons.infrastructure.intercommunication.dto.messaging.A6InfraMessageDto
import com.angorasix.messaging.messaging.handler.MessagingMessagingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
@Configuration // spring-cloud-streams is not prepared to handle Kotlin DSL beans: https://github.com/spring-cloud/spring-cloud-stream/issues/2025
class MessagingMessagingRouter(val handler: MessagingMessagingHandler) {
    @Bean
    fun clubInvitation(): (A6InfraMessageDto) -> Unit =
        { handler.clubContributorInvitation(it) }
}
