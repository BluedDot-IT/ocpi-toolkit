//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Evse]
 */
public data class EvsePartial(
  public val uid: String?,
  public val evse_id: String?,
  public val status: Status?,
  public val status_schedule: List<StatusSchedulePartial>?,
  public val capabilities: List<Capability>?,
  public val connectors: List<ConnectorPartial>?,
  public val floor_level: String?,
  public val coordinates: GeoLocationPartial?,
  public val physical_reference: String?,
  public val directions: List<DisplayTextPartial>?,
  public val parking_restrictions: List<ParkingRestriction>?,
  public val images: List<ImagePartial>?,
  public val last_updated: Instant?,
)

public fun Evse.toPartial(): EvsePartial {
   return EvsePartial(
     uid = uid,
    evse_id = evse_id,
    status = status,
    status_schedule = status_schedule.toPartial(),
    capabilities = capabilities,
    connectors = connectors.toPartial(),
    floor_level = floor_level,
    coordinates = coordinates?.toPartial(),
    physical_reference = physical_reference,
    directions = directions.toPartial(),
    parking_restrictions = parking_restrictions,
    images = images.toPartial(),
    last_updated = last_updated
   )
}

public fun List<Evse>.toPartial(): List<EvsePartial> {
   return mapNotNull { it.toPartial() }
}
