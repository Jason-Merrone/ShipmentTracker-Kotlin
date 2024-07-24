package shipmentFactory

import updateStrategies.NoteAddedUpdatePattern
import kotlin.math.abs

class BulkShipment(
    status:String,
    id:String,
    timestamp:Long,
): Shipment(status,id,timestamp) {

    override fun updateExpectedDeliveryDate(date: Long?) {
        if (date != null && abs(date - dateCreated) < oneDayInMillis*3) {
            NoteAddedUpdatePattern().updateShipment(id,status,timestamp,"Shipment expected sooner than the required 3 days waiting for bulk shipments")
        }
        expectedDeliverDateTimestamp = date;
    }
}