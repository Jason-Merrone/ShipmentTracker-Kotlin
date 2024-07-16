class NoteAddedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.updateStatus("noteadded")
        shipment?.addNote(otherInfo)
        if(shipment != null)
            TrackingSimulator.addShipment(shipment)
    }
}