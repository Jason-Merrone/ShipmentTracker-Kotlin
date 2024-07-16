class LocationUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.updateStatus("location")
        shipment?.currentLocation = otherInfo
        if(shipment != null)
            TrackingSimulator.addShipment(shipment)
    }
}