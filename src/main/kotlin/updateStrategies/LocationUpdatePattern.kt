package updateStrategies

import TrackingSimulator

class LocationUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
        val shipment = TrackingSimulator.findShipment(id)
        shipment?.currentLocation = otherInfo
        shipment?.notifyObservers()
    }
}