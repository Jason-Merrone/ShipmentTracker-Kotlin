class DelayedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.updateStatus("delayed")
        shipment?.expectedDeliverDateTimestamp = otherInfo?.toLong()
        if(shipment != null)
            TrackingSimulator.addShipment(shipment)
    }
}