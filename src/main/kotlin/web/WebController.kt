package web

import data.Trainer
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
import bot.sendBotMessage
import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.long
import configs
import data.getSettings
import io.ktor.request.header
import io.ktor.routing.get
import me.ivmg.telegram.entities.ParseMode
import java.net.URLDecoder

private val gson = Gson()

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

                trainersFullMessages.parallelStream().forEach {
                    sendBotMessage(it.first, it.second)
                }

                val managerMessage = prepareManagerMessage(trainersSplit, distributionHeader)

                configs["bot"]["manager_list"].array.toList().parallelStream().forEach {
                    sendBotMessage(it.long, managerMessage)
                }

                call.respond(HttpStatusCode.OK, trainersFullMessages)
            }

            get("/elitesochi/get_settings") {
                call.respond(getSettings())
            }

            post("/elitesochi/broadcast_table_data"){
                val data = gson.fromJson<List<Map<String, String>>>(call.receive(JsonArray::class))
                val header = URLDecoder.decode(call.request.header("Table-Header")!!, "UTF-8")
                val chatIds = call.request.header("Chat-Ids")!!.split(';').map { it.toLong() }

                val message = prepareBroadcastMessage(data, header)

                chatIds.parallelStream().forEach {
                    sendBotMessage(it, message, null)
                }

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
