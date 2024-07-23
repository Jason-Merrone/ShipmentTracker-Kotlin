package updateStrategies

class LocationUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = Server.findShipment(id)
        shipment?.currentLocation = otherInfo
        shipment?.notifyObservers()
    }
}