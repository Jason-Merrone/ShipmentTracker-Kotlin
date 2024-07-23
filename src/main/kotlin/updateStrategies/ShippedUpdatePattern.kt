package updateStrategies

class ShippedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = Server.findShipment(id)
        shipment?.updateStatus("shipped")
        shipment?.expectedDeliverDateTimestamp = otherInfo?.toLong()
        shipment?.notifyObservers()
    }
}