import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import updateStrategies.*
import java.io.File

object Server {
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