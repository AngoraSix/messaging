package com.angorasix.messaging

import com.angorasix.messaging.application.MessagingService
import com.angorasix.messaging.messaging.handler.MessagingMessagingHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val beans = beans {
    bean<MessagingService>()
    bean<MessagingMessagingHandler>()
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) =
        com.angorasix.messaging.beans.initialize(context)
}
