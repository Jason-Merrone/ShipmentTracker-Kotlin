import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import java.io.File

object Server {
    fun runServer(){
        embeddedServer(Netty, 8080) {
            routing {
                get("/") {
                    call.respondText(File("index.html").readText(), ContentType.Text.Html)
                }
                post("/data"){
                    val data = call.receiveText()
                    call.respondText {"Success"}
                }
            }
        }.start(wait = true)
    }
}