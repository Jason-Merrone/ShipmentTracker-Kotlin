package shipmentFactory

object ShipmentFactory {
    fun createShipment(status:String,id:String,type:String?,timeStamp:Long):Shipment{
        val shipmentTypes: Map<String, Shipment> = mapOf(
            "standard" to StandardShipment(status,id,timeStamp),
            "express" to ExpressShipment(status,id,timeStamp),
            "overnight" to OvernightShipment(status,id,timeStamp),
            "bulk" to BulkShipment(status,id,timeStamp),
        )
        // Always assumes a standard shipment if type is invalid
        return shipmentTypes[type] ?: StandardShipment(status, id, timeStamp)
    }
}