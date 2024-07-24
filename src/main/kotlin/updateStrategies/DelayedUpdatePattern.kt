package updateStrategies

class DelayedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.updateStatus("delayed")
        shipment?.updateExpectedDeliveryDate(otherInfo?.toLong())
        shipment?.notifyObservers()
    }
}