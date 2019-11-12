package web

import io.ktor.network.tls.certificates.generateCertificate
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import java.io.File

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

