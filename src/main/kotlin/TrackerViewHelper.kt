class TrackerViewHelper{


    private val trackedShipments = mutableMapOf<String, Shipment>()

    fun trackShipment(shipmentId:String){
//        shipmentDataList[shipmentId] = Shipment
    }

    fun stopTracking(shipmentId:String){
        trackedShipments.remove(shipmentId)
    }

    fun updateShipmentData(shipmentId:String, ){
    }
}