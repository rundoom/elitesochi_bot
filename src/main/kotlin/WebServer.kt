import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.network.tls.certificates.generateCertificate
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import java.io.File

val hardcodedUserCredentials = mapOf<String?, String>("SQLServer" to "yTmLA(=8PRyjv;PP")
val gson = Gson()

fun startServer() {
    val file = File("sert/temporary.jks")

    file.parentFile.mkdirs()
    val keyStore = generateCertificate(
        file = file
    )

    embeddedServer(
        Netty,
        environment = applicationEngineEnvironment {
            module {
                serve()
            }
            sslConnector(
                keyStore = keyStore,
                keyAlias = "mykey",
                keyStorePassword = { "changeit".toCharArray() },
                privateKeyPassword = { "changeit".toCharArray() }) {
                host = "0.0.0.0"
                port = 9090
            }
        }
    ).start(wait = true)
}

fun Application.serve() {
    install(Authentication) {
        basic("SQL Server") {
            realm = "ktor"
            validate { credentials ->
                if (credentials.password == hardcodedUserCredentials[credentials.name])
                    UserIdPrincipal(credentials.name) else null
            }
        }
    }
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }

    routing {
        authenticate("SQL Server") {
            post("/elitesochi/send_messages") {
//                val trainers = call.receive<List<Trainer>>()
                val trainers = gson.fromJson<List<Trainer>>(call.receive<JsonArray>())
                val trainerMessages = generateTrainerMessages(trainers)
            }
        }
    }
}
