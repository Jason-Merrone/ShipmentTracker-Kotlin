package updateStrategies

class LocationUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.currentLocation = otherInfo
        shipment?.notifyObservers()
    }
}