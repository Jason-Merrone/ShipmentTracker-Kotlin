package updateStrategies

class NoteAddedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = ShipmentManager.findShipment(id)
        shipment?.addNote(otherInfo)
        shipment?.notifyObservers()
    }
}