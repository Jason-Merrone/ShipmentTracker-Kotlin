class Shipment(
    private var status:String,
    val id:String,
    val timestamp:Long,
    ):ShipmentSubject{

    private var notes: MutableList<String?> = mutableListOf()
    private val updateHistory: MutableList<String> = mutableListOf("created")
    var expectedDeliverDateTimestamp: Long? = null
    var currentLocation:String? = null
    private val observers: MutableList<ShipmentObserver> = mutableListOf()

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

    override fun subscribe(observer: ShipmentObserver) {
        observers.add(observer)
    }

    override fun unsubscribe(observer: ShipmentObserver) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.notify(this) }
    }
}