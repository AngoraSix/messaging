package com.angorasix.messaging.application

import com.angorasix.commons.infrastructure.intercommunication.dto.invitations.A6InfraClubInvitation
import com.angorasix.messaging.infrastructure.config.configurationproperty.a6infra.A6InfraConfigurations

/**
 *
 *
 * @author rozagerardo
 */
class MessagingService(
    private val infraConfigs: A6InfraConfigurations,
) {
    suspend fun processContributorInvitation(invitation: A6InfraClubInvitation) {
        val tokenUrl = infraConfigs.patternUrls.invitationUrlPattern
            .replace(infraConfigs.patternPlaceholders.clubId, invitation.club.id)
            .replace(infraConfigs.patternPlaceholders.invitationToken, invitation.token)
        println("Generated token URL: $tokenUrl")
        // send email
    }
}
