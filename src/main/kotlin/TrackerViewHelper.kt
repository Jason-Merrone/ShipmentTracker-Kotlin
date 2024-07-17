import androidx.compose.runtime.mutableStateListOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TrackerViewHelper : ShipmentObserver {
    val updateHistory = mutableStateListOf<List<String>>()
    val expectedDeliveryDate = mutableStateListOf<String?>()
    val status = mutableStateListOf<String?>()
    val location = mutableStateListOf<String?>()
    val notes = mutableStateListOf<List<String?>>()
    var timestamp = mutableStateListOf<String?>()
        private set

    private val shipmentIds = mutableStateListOf<String>()

    private fun formatTime(time: Long?): String {
        return if (time != null) {
            val dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            dateTime.format(formatter)
        } else {
            "N/A"
        }
    }

    fun trackShipment(shipment: Shipment) {
        shipment.subscribe(this)

        if (!shipmentIds.contains(shipment.id)) {
            updateHistory.add(shipment.getUpdateHistory())
            expectedDeliveryDate.add(formatTime(shipment.expectedDeliverDateTimestamp))
            status.add(shipment.getStatus())
            location.add(shipment.currentLocation)
            notes.add(shipment.getNotes())
            shipmentIds.add(shipment.id)
            timestamp.add(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
        }
    }

    fun stopTracking(shipment: Shipment) {
        val index = findShipmentIndex(shipment)
        if (index != -1) {
            updateHistory.removeAt(index)
            expectedDeliveryDate.removeAt(index)
            status.removeAt(index)
            location.removeAt(index)
            notes.removeAt(index)
            shipmentIds.removeAt(index)
            timestamp.removeAt(index)
        }
        shipment.unsubscribe(this)
    }

    override fun notify(shipment: Shipment?) {
        if (shipment != null) {
            val index = findShipmentIndex(shipment)
            if (index != -1) {
                updateHistory[index] = shipment.getUpdateHistory()
                expectedDeliveryDate[index] = formatTime(shipment.expectedDeliverDateTimestamp)
                status[index] = shipment.getStatus()
                location[index] = shipment.currentLocation
                notes[index] = shipment.getNotes()
                timestamp[index] = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
            } else {
                println("Warning: untracked shipment ${shipment.id}; I'm scared")
            }
        }
    }

    private fun findShipmentIndex(shipment: Shipment): Int {
        return shipmentIds.indexOf(shipment.id)
    }
}
