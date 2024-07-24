package shipmentFactory

import ShipmentObserver
import ShipmentSubject

abstract class Shipment(
    protected var status:String,
    val id:String,
    val timestamp:Long,
    ): ShipmentSubject {
        protected val dateCreated:Long = timestamp
        protected val oneDayInMillis = 24 * 60 * 60 * 1000L // Calculate milliseconds in 3 days

        private var notes: MutableList<String?> = mutableListOf()
        private val updateHistory: MutableList<String> = mutableListOf("created")

        var expectedDeliverDateTimestamp: Long? = null
            protected set

        var currentLocation:String? = null
        private val observers: MutableList<ShipmentObserver> = mutableListOf()

        abstract fun updateExpectedDeliveryDate(date: Long?)

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

        fun retrieveStatus():String{
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