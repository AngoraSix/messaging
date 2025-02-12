package com.angorasix.messaging.infrastructure.email.templating

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class ThymeleafConfig {

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        // "messages/messages" means it'll look for messages.properties, messages_en.properties, etc.
        messageSource.setBasenames("messages/messages")
        messageSource.setDefaultEncoding("UTF-8")
        // If a key doesn't exist, throw an error or show ???key???
        messageSource.setUseCodeAsDefaultMessage(true)
        return messageSource
    }

    /**
     * If you want to read Thymeleaf templates from the /templates folder.
     * If using a "headless" approach, you can also do a ClassLoader-based resolver.
     */
    @Bean
    fun templateResolver(): ITemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        // For a typical Spring Boot app, "classpath:templates/" is the default location.
        templateResolver.prefix = "classpath:/templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = false // Turn off for dev, consider enabling in prod
        return templateResolver
    }

    @Bean
    fun templateEngine(
        templateResolver: ITemplateResolver,
        messageSource: ResourceBundleMessageSource,
    ): SpringTemplateEngine {
        val engine = SpringTemplateEngine()
        engine.setTemplateResolver(templateResolver)
        engine.setTemplateEngineMessageSource(messageSource)
        return engine
    }
}
