package updateStrategies

class LostUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.updateStatus("lost")
        shipment?.notifyObservers()
    }
}