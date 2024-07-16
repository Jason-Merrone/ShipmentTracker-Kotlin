class ShipmentUpdateProcessor {
    private val updateStrategies: Map<String, ShippingUpdateStrategy> = mapOf(
        "created" to CreatedUpdatePattern(),
        "shipped" to ShippedUpdatePattern(),
        "location" to LocationUpdatePattern(),
        "delayed" to DelayedUpdatePattern(),
        "noteadded" to NoteAddedUpdatePattern(),
        "lost" to LostUpdatePattern(),
        "canceled" to CanceledUpdatePattern(),
        "delivered" to DeliveredUpdatePattern(),
    )

    fun processUpdate(strategy:String, shipment:Shipment, timeStamp: String, otherInfo: String){
        val currentStrategy = updateStrategies[strategy]
        if (strategy == "created") {

        }
    }
}