import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import shipmentFactory.Shipment
import updateStrategies.*
import java.io.File

object ShipmentManager {
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
        shipment.notifyObservers()
    }

    private fun processUpdate(update:String){
        val parts = update.split(",", limit = 5)
        val updateType = parts[0]
        val shipmentId = parts[1]
        val timestamp = parts[3].toLongOrNull() ?: 0L
        val otherInfo = parts.getOrNull(4) ?: ""

        val strategy = updateStrategies[updateType]
        strategy?.updateShipment(shipmentId,findShipment(shipmentId)?.retrieveStatus(),timestamp.toLong(),otherInfo)
    }

    fun runServer(){
        embeddedServer(Netty, 8080) {
            routing {
                get("/") {
                    call.respondText(File("index.html").readText(), ContentType.Text.Html)
                }
                post("/data"){
                    val data = call.receiveText()
                    processUpdate(data)
                    call.respondText {"Success"}
                }
            }
        }.start(wait = true)
    }
}