package updateStrategies

class ShippedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.updateStatus("shipped")
        shipment?.updateExpectedDeliveryDate(otherInfo?.toLong())
        shipment?.notifyObservers()
    }
}