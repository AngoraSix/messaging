package com.angorasix.messaging.presentation.handler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

class MessagingHandler {
    suspend fun wakeup(request: ServerRequest): ServerResponse {
        println(request.path())
        return ServerResponse.noContent().buildAndAwait()
    }
}
