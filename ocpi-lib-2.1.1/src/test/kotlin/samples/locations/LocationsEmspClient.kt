package samples.locations

import ocpi.locations.LocationsEmspClient
import samples.common.Http4kTransportClient

/**
 * Example on how to use the eMSP client
 */
fun main() {
    // We specify the transport client to communicate with the CPO
    val transportClient = Http4kTransportClient(cpoServerUrl)

    // We instantiate the clients that we want to use
    val locationsEmspClient = LocationsEmspClient(transportClient)

    // We can use it
    println(
        locationsEmspClient.getConnector(CREDENTIALS_TOKEN_C, "location1", "evse1", "connector1")
    )
}