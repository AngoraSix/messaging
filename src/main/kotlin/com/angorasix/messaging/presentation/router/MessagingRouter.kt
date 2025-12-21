package com.angorasix.messaging.presentation.router

import com.angorasix.commons.reactive.presentation.filter.extractRequestingContributor
import com.angorasix.messaging.infrastructure.config.api.ApiConfigs
import com.angorasix.messaging.presentation.handler.MessagingHandler
import org.springframework.web.reactive.function.server.coRouter

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class MessagingRouter(
    private val messagingHandler: MessagingHandler,
    private val apiConfigs: ApiConfigs,
) {
    /**
     * Main RouterFunction configuration for all endpoints related to Clubs.
     *
     * @return the RouterFunction with all the routes for Clubs
     */
    fun messagingRouterFunction() =
        coRouter {
            apiConfigs.basePaths.messaging.nest {
                filter { request, next ->
                    extractRequestingContributor(
                        request,
                        next,
                    )
                }
                path(apiConfigs.routes.wakeup.path).nest {
                    method(apiConfigs.routes.wakeup.method, messagingHandler::wakeup)
                }
            }
        }
}
