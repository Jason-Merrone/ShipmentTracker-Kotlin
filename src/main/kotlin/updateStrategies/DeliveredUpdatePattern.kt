package updateStrategies

class DeliveredUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = Server.findShipment(id)
        shipment?.updateStatus("delivered")
        shipment?.notifyObservers()
    }
}