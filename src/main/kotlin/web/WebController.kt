package web

import bot.sendBotMessage
import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.long
import com.google.gson.Gson
import com.google.gson.JsonArray
import configs
import data.Trainer
import data.getStuckInDistribution
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.escapeHTML
import me.ivmg.telegram.entities.ParseMode
import java.io.File
import java.net.URLDecoder

private val gson = Gson()

fun Route.sendStuckInDistribution() = post("/elitesochi/send_messages") {
    val trainers = gson.fromJson<List<Trainer>>(call.receive(JsonArray::class))

    val trainersSplit = splitByTrainersSegments(trainers)
    val distributionHeader = getStuckInDistribution()

    trainersSplit.forEach {
        val hbsData = mapOf(
            "stucks" to distributionHeader,
            "trainers" to it
        )

        val trainerFullMessage = applyFromHbs(hbsData, File("templates/user_message.hbs").readText(), "ЗАВИСЛИ В РАЗДАЧЕ")
        sendBotMessage(it.entries.first().value.first().chatId, trainerFullMessage, ParseMode.HTML)
    }

    val hbsManagerData = mapOf(
        "stucks" to distributionHeader,
        "trainers" to prepareManagerData(trainersSplit)
    )

    val managerMessage = applyFromHbs(hbsManagerData, File("templates/manager_message.hbs").readText(), "ЗАВИСЛИ В РАЗДАЧЕ")

    configs["bot"]["manager_list"].array.toList().parallelStream().forEach {
        sendBotMessage(it.long, managerMessage, ParseMode.HTML)
    }

    call.respond(HttpStatusCode.OK)
}

fun Route.getSettings() = get("/elitesochi/get_settings") {
    call.respond(data.getSettings())
}

fun Route.broadcastTableData() = post("/elitesochi/broadcast_table_data") {
    val data = gson.fromJson<List<Map<String, String>>>(call.receive(JsonArray::class))
    val header = URLDecoder.decode(call.request.header("Table-Header")!!.escapeHTML(), "UTF-8")
    val chatIds = call.request.header("Chat-Ids")!!.split(';').map { it.toLong() }
    val isSingleFieldDeprecated = call.request.header("Deprecate-Single") != null

    val message = if (isSingleFieldDeprecated || data.any { it.size > 1 }) {
        applyFromHbs(data, File("templates/table_data_multiple.hbs").readText(), header)
    } else {
        applyFromHbs(data, File("templates/table_data_single.hbs").readText(), header)
    }

    chatIds.parallelStream().forEach {
        sendBotMessage(it, message, ParseMode.HTML)
    }

    call.respond(HttpStatusCode.OK)
}

fun Route.broadcastRawMessage() = post("/elitesochi/broadcast_raw_message") {
    val data = call.receiveStream().bufferedReader().use { it.readText() }
    val header = call.request.header("Table-Header")?.let { URLDecoder.decode(it, "UTF-8") }

    val chatIds = call.request.header("Chat-Ids")!!.split(';').map { it.toLong() }
    val message = applyFromHbs(data, File("templates/raw_message.hbs").readText(), header)

    chatIds.parallelStream().forEach {
        sendBotMessage(it, message, ParseMode.HTML, true)
    }

    call.respond(HttpStatusCode.OK)
}