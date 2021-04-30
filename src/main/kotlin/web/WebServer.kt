package web

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.string
import configs

import io.ktor.network.tls.certificates.generateCertificate
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import java.io.File

val userCredentials = configs["credentials"].array.map { it["username"].string to it["password"].string }.toMap()

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
                keyAlias = configs["ssl"]["keyAlias"].string,
                keyStorePassword = { configs["ssl"]["keyStorePassword"].string.toCharArray() },
                privateKeyPassword = { configs["ssl"]["privateKeyPassword"].string.toCharArray() }) {
                host = configs["web"]["host"].string
                port = configs["web"]["port"].int
            }
        }
    ).start(wait = true)
}

