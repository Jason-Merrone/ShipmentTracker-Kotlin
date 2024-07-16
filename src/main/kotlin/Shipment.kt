class Shipment(
    private var status:String,
    val id:String,
    val timestamp:Long,
    ){

    private var notes: MutableList<String?> = mutableListOf()
    private val updateHistory: MutableList<String> = mutableListOf("created")
    var expectedDeliverDateTimestamp: Long? = null
    var currentLocation:String? = null

    fun updateStatus(newStatus:String){
        updateHistory.add(newStatus)
        status = newStatus
    }

    fun addNote(note:String?){
        notes.add(note)
    }

    fun getNotes():List<String?>{
        return notes.toList()
    }

    fun getUpdateHistory():List<String>{
        return updateHistory.toList()
    }

    fun getStatus():String{
        return status
    }

    fun clone(): Shipment {
        val clonedShipment = Shipment(status, id, timestamp)
        clonedShipment.notes.addAll(this.notes)
        clonedShipment.updateHistory.addAll(this.updateHistory)
        clonedShipment.expectedDeliverDateTimestamp = this.expectedDeliverDateTimestamp
        clonedShipment.currentLocation = this.currentLocation
        return clonedShipment
    }
}