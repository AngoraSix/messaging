package com.angorasix.messaging.infrastructure.config.configurationproperty.a6infra

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * <p>
 *  Base file containing all Service configurations.
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "configs.infra")
data class A6InfraConfigurations(

    @NestedConfigurationProperty
    var patternUrls: PatternUrls,

    @NestedConfigurationProperty
    var patternPlaceholders: PatternPlaceholders,
)

class PatternUrls(
    val invitationUrlPattern: String,
)

class PatternPlaceholders(
    val clubId: String,
    val invitationToken: String,
)
