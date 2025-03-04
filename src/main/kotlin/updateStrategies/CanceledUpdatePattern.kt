package updateStrategies

class CanceledUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.updateStatus("canceled")
        shipment?.notifyObservers()
    }
}