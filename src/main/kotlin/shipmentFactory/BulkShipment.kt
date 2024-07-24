package shipmentFactory

import updateStrategies.NoteAddedUpdatePattern
import kotlin.math.abs

class BulkShipment(
    status:String,
    id:String,
    timestamp:Long,
): Shipment(status,id,timestamp) {
    override fun updateExpectedDeliveryDate(date: Long?) {
        if (date != null && date - dateCreated <= 259200000 && status != "delayed") {
            NoteAddedUpdatePattern().updateShipment(id,status,timestamp,"Shipment is expected earlier than the required delay period of 3 days for bulk shipments")
        }
        expectedDeliverDateTimestamp = date;
    }
}