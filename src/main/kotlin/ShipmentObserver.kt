import shipmentFactory.Shipment

interface ShipmentObserver {
    fun notify (shipment: Shipment?)
}