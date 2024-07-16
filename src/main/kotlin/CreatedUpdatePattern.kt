class CreatedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = Shipment("created",id,timestamp)
        TrackingSimulator.addShipment(shipment)
    }
}