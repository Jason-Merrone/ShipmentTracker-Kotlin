package updateStrategies

import shipmentFactory.Shipment
import shipmentFactory.ShipmentFactory

class CreatedUpdatePattern(): ShippingUpdateStrategy {
    override fun updateShipment(id: String, previousStatus: String?, timestamp: Long, otherInfo: String?) {
//        val shipment = Shipment("created", id, timestamp)
        val shipment = ShipmentFactory.createShipment("created",id,otherInfo,timestamp)
        ShipmentManager.addShipment(shipment)
    }
}