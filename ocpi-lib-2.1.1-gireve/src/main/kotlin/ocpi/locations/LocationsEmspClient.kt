package ocpi.locations

import common.*
import ocpi.credentials.repositories.PlatformRepository
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClient
 */
class LocationsEmspClient(
    private val transportClient: TransportClient,
    private val platformRepository: PlatformRepository
) : LocationsCpoInterface {

    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations",
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap(),
                    headers = mapOf(platformRepository.buildAuthorizationHeader(transportClient))
                )
            )
            .parsePaginatedBody(offset)

    override fun getLocation(locationId: String): OcpiResponseBody<Location?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId",
                    headers = mapOf(platformRepository.buildAuthorizationHeader(transportClient))
                )
            )
            .parseBody()

    override fun getEvse(locationId: String, evseUid: String): OcpiResponseBody<Evse?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId/$evseUid",
                    headers = mapOf(platformRepository.buildAuthorizationHeader(transportClient))
                )
            )
            .parseBody()

    override fun getConnector(
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId/$evseUid/$connectorId",
                    headers = mapOf(platformRepository.buildAuthorizationHeader(transportClient))
                )
            )
            .parseBody()
}
