import kotlinx.coroutines.*
import java.io.File

object TrackingSimulator {
    private val shipments = mutableMapOf<String, Shipment>()
    private val updateStrategies: Map<String, ShippingUpdateStrategy> = mapOf(
        "created" to CreatedUpdatePattern(),
        "shipped" to ShippedUpdatePattern(),
        "location" to LocationUpdatePattern(),
        "delayed" to DelayedUpdatePattern(),
        "noteadded" to NoteAddedUpdatePattern(),
        "lost" to LostUpdatePattern(),
        "canceled" to CanceledUpdatePattern(),
        "delivered" to DeliveredUpdatePattern(),
    )

    fun findShipment(id: String?) : Shipment? {
        return shipments[id]
    }
    fun addShipment(shipment: Shipment) {
        shipments[shipment.id] = shipment
    }

    private fun processUpdate(update:String){
        val parts = update.split(",", limit = 4)
        val updateType = parts[0]
        val shipmentId = parts[1]
        val timestamp = parts[2].toLongOrNull() ?: 0L
        val otherInfo = parts.getOrNull(3) ?: ""

        val strategy = updateStrategies[updateType]
        strategy?.updateShipment(shipmentId,findShipment(shipmentId)?.getStatus(),timestamp.toLong(),otherInfo)
    }

    suspend fun runSimulation() = runBlocking{
        val fileName = "test.txt"
        val file = File(fileName)

        if (!file.exists()) {
            println("Error: File not found: $fileName")
            return@runBlocking
        }

        val lines = file.readLines() // Read lines into a list

        for (line in lines) {
            delay(1000L)
            println(line)
            processUpdate(line)
        }
    }
}