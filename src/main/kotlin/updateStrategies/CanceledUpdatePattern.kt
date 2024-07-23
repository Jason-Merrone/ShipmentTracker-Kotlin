package updateStrategies

import TrackingSimulator

class CanceledUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.updateStatus("canceled")
        shipment?.notifyObservers()
    }
}