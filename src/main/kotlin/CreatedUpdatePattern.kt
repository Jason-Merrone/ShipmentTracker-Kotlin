class CreatedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(shipment: Shipment, timestamp: Long, otherInfo: String?): ShippingUpdate {

        return ShippingUpdate(null,"created",timestamp)
    }
}