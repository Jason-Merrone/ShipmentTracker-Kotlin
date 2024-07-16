
class Shipment(
    var status:String,
    val id:String,
    private var notes:String,
    private var updateHistory:MutableList<ShippingUpdate>,
    var expectedDeliverDateTimestamp:Long?,
    var currentLocation:String?
    ){



    fun addUpdate(update:ShippingUpdate){
        updateHistory.add(update)

    }

    fun getNotes():String{
        return notes
    }

    fun getUpdateHistory():MutableList<ShippingUpdate>{
        return updateHistory
    }
}