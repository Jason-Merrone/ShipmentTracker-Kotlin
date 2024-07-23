package updateStrategies

interface ShippingUpdateStrategy {
    fun updateShipment(id:String, previousStatus:String? = null, timestamp: Long, otherInfo: String? = null)
}