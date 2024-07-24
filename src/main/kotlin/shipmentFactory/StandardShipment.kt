package shipmentFactory

class StandardShipment(
    status:String,
    id:String,
    timestamp:Long,
): Shipment(status,id,timestamp) {
    override fun updateExpectedDeliveryDate(date: Long?) {
        expectedDeliverDateTimestamp = date;
    }
}