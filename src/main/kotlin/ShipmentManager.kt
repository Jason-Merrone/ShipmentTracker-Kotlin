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

    fun processUpdate(update: String): Pair<String, HttpStatusCode> {
        val parts = update.split(",", limit = 4)
        if (parts.size < 3) {
            return "Error: Too few parameters. Expected format: <updateType>,<shipmentId>,<timestamp>,<otherInfo>" to HttpStatusCode.BadRequest
        }

        val updateType = parts[0]
        val shipmentId = parts[1]
        val timestamp = parts[2].toLongOrNull() ?: return "Error: Invalid timestamp format" to HttpStatusCode.BadRequest
        val otherInfo = parts.getOrNull(3)

        val strategy = updateStrategies[updateType] ?: return "Error: Invalid update type" to HttpStatusCode.BadRequest

        if(findShipment(shipmentId) != null && updateType == "created") {
            return "Error: Duplicate ID" to HttpStatusCode.BadRequest
        }

        val shipment = findShipment(shipmentId)

        strategy.updateShipment(shipmentId, shipment?.retrieveStatus(), timestamp, otherInfo)
        return "Success" to HttpStatusCode.OK
    }

    fun runServer() {
        embeddedServer(Netty, 8080) {
            routing {
                get("/") {
                    call.respondText(File("index.html").readText(), ContentType.Text.Html)
                }
                post("/data") {
                    val data = call.receiveText()
                    val (result, status) = processUpdate(data)
                    call.respondText(result, ContentType.Text.Plain, status)
                }
            }
        }.start(wait = true)
    }
}