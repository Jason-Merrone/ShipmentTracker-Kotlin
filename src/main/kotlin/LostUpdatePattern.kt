class LostUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.updateStatus("lost")
        if(shipment != null)
            TrackingSimulator.addShipment(shipment)
    }
}