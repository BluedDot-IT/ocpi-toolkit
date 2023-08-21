package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.samples.common.*

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val receiverVersionsUrl = "http://localhost:$receiverPort/versions"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    // Add token A associated with the sender
    val receiverPlatformRepository = PlatformCacheRepository()
    receiverPlatformRepository.platforms[receiverVersionsUrl] = Platform(url = receiverVersionsUrl, tokenA = tokenA)

    val receiverServer = Http4kTransportServer(
        baseUrl = receiverUrl,
        port = receiverPort,
        secureFilter = receiverPlatformRepository::tokenFilter
    )

    CredentialsServer(
        service = CredentialsServerService(
            platformRepository = receiverPlatformRepository,
            credentialsRoleRepository = object : CredentialsRoleRepository {
                override fun getCredentialsRoles(): List<CredentialRole> = listOf(
                    CredentialRole(
                        role = Role.EMSP,
                        business_details = BusinessDetails(name = "Receiver", website = null, logo = null),
                        party_id = "DEF",
                        country_code = "FR"
                    )
                )
            },
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsUrl = receiverVersionsUrl
        )
    ).registerOn(receiverServer)
    VersionsServer(
        service = VersionsValidationService(
            repository = VersionsCacheRepository(baseUrl = receiverUrl)
        )
    ).registerOn(receiverServer)

    VersionDetailsServer(
        service = VersionDetailsValidationService(
            repository = VersionDetailsCacheRepository(baseUrl = receiverUrl)
        )
    ).registerOn(receiverServer)
    receiverServer.start()
}
