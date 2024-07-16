interface ShippingUpdateStrategy {
    fun updateShipment(shipment: Shipment, timestamp: Long, otherInfo: String? = null): ShippingUpdate
}