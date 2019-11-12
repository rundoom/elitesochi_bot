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

val managersList = listOf<Long>(153174359)

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
                val trainers = gson.fromJson<List<Trainer>>(call.receive(JsonArray::class))

                val trainersSplit = splitByTrainersSegments(trainers)
                val trainersMessages = generateTrainerMessagesBody(trainersSplit)
                val distributionHeader = formatStuckInDistribution()

                val trainersFullMessages = prepareTrainerFullMessages(trainersMessages, distributionHeader)

                trainersFullMessages.forEach {
                    sendBotMessage(it.first, it.second)
                }

                val managerMessage = prepareManagerMessage(trainersSplit, distributionHeader)

                managersList.forEach {
                    sendBotMessage(it, managerMessage)
                }

                call.respond(HttpStatusCode.OK, trainersFullMessages)
            }
        }
    }
}
