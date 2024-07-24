package shipmentFactory

import updateStrategies.NoteAddedUpdatePattern
import kotlin.math.abs

class OvernightShipment(
    status:String,
    id:String,
    timestamp:Long,
): Shipment(status,id,timestamp) {
    override fun updateExpectedDeliveryDate(date: Long?) {
        if (date != null && abs(date - dateCreated) > oneDayInMillis && status != "delayed") {
            NoteAddedUpdatePattern().updateShipment(id,status,timestamp,"Shipment expected later than the required delivery date of 3 days")
        }
        expectedDeliverDateTimestamp = date;
    }
}