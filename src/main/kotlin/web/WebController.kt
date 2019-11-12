package web

import Trainer
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.ktor.application.Application
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
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import sendBotMessage

val gson = Gson()

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
                val trainers = gson.fromJson<List<Trainer>>(call.receive(JsonArray::class))

                val trainerMessages = generateTrainerMessagesBody(trainers)
                val distributionHeader = formatStuckInDistribution()

                val trainersFullMessages = prepareTrainerFullMessages(trainerMessages, distributionHeader)

                trainersFullMessages.forEach {
                    sendBotMessage(it.first, it.second)
                }

                call.respond(HttpStatusCode.OK, trainersFullMessages)
            }
        }
    }
}
