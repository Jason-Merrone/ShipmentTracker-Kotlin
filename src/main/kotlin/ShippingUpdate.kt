class ShippingUpdate(
    val previousStatus: String?,
    val newStatus: String,
    val timeStamp: Long,
    val expectedDeliveryDateTimeStamp: Long?,
    val currentLocation:String?
)