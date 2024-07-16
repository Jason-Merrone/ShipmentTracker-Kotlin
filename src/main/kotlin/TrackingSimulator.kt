import kotlinx.coroutines.*
import java.io.File

object TrackingSimulator {
    private val shipments = mutableMapOf<String, Shipment>()

    fun findShipment(id: String) : Shipment? {
        return shipments[id]
    }
    fun addShipment(shipment: Shipment) {
        shipments[shipment.id] = shipment
    }
    suspend fun runSimulation() = runBlocking{
        val fileName = "test.txt"
        val file = File(fileName)
        val updater = ShipmentUpdateProcessor()

        val lines = file.readLines() // Read lines into a list

        for (line in lines) {
            delay(1000L)
            println(line)
            val sections = line.split(',')
            val updateType = sections.getOrNull(0) ?: ""
            val shipmentId = sections.getOrNull(1) ?: ""
            val timestampOfUpdate = sections.getOrNull(2) ?: ""
            val otherInfo = if (sections.size > 3) sections.subList(3, sections.size).joinToString(",") else ""

            if(updateType == "created"){

            }
        }
    }
}