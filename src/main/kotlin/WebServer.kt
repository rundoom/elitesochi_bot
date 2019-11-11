import io.ktor.application.Application
import io.ktor.application.ApplicationEvents
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.config.ApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.HttpsRedirect
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.network.tls.certificates.generateCertificate
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.typeOf

val hardcodedUserCredentials = mapOf<String?, String>("SQLServer" to "yTmLA(=8PRyjv;PP")

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
                println(credentials)
                if (credentials.password == hardcodedUserCredentials[credentials.name])
                    UserIdPrincipal(credentials.name) else null
            }
        }
    }
    install(ContentNegotiation) {
        gson()
    }

    routing {
        authenticate("SQL Server") {
            post("/elitesochi/send_messages") {
                val receive = call.receive<List<Trainer>>()
                call.respondText { "lil" }
            }
        }
    }
}