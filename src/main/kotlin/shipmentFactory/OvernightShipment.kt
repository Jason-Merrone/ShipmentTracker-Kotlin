package shipmentFactory

import updateStrategies.NoteAddedUpdatePattern
import kotlin.math.abs

class OvernightShipment(
    status:String,
    id:String,
    timestamp:Long,
): Shipment(status,id,timestamp) {
    override fun updateExpectedDeliveryDate(date: Long?) {
        if (date != null && date - dateCreated > 86400000 && status != "delayed") {
            NoteAddedUpdatePattern().updateShipment(id,status,timestamp,"Shipment is expected later than the required delivery date of 24 hours for overnight shipments")
        }
        expectedDeliverDateTimestamp = date;
    }
}