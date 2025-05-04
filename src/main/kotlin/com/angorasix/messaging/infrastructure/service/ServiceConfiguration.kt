package com.angorasix.messaging.infrastructure.service

import com.angorasix.messaging.application.MessagingService
import com.angorasix.messaging.infrastructure.config.configurationproperty.a6infra.A6InfraConfigurations
import com.angorasix.messaging.messaging.listener.handler.MessagingMessagingHandler
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine

@Configuration
class ServiceConfiguration {
    @Bean
    fun messagingService(
        infraConfigs: A6InfraConfigurations,
        mailSender: JavaMailSender,
        templateEngine: TemplateEngine,
        messageSource: MessageSource,
    ) = MessagingService(infraConfigs, mailSender, templateEngine, messageSource)

    @Bean
    fun messagingMessagingHandler(messagingService: MessagingService) = MessagingMessagingHandler(messagingService)
}
