package web

import data.Trainer
import data.getStuckInDistribution
import io.ktor.util.escapeHTML


fun splitByTrainersSegments(trainers: List<Trainer>): List<Map<String, List<Trainer>>> {
    val trainersByChatId = trainers.groupBy { it.chatId }.toList()

    return trainersByChatId.map { chatToUsers ->
        chatToUsers.second.groupBy { it.segment }
    }
}

fun generateTrainerMessagesBody(trainers: List<Map<String, List<Trainer>>>): List<Pair<Long, String>> {
    return trainers.map { segmented ->
        segmented.values.first().first().chatId to segmented.toList().joinToString("\n------------------") { paired ->
            "\n${paired.first}:\n\n" +
                    paired.second.sortedBy { it.got }.joinToString("\n\n") { "${it.username}\n${it.phone}" }
        } + "\n------------------"
    }
}

fun prepareTrainerFullMessages(bodyMessages: List<Pair<Long, String>>, header: String): List<Pair<Long, String>> {
    return bodyMessages.map {
        it.first to "$header\n------------------\n<b>ИХ МОЖНО ПРИЗВАТЬ:</b>${it.second.escapeHTML()}\n$header"
    }
}

fun formatStuckInDistribution(): String {
    return "<b>ЗАВИСЛИ В РАЗДАЧЕ:</b>\n" + getStuckInDistribution().joinToString("\n") {
        "${it.ruleName}: ${it.dealCount}"
    }
}

fun prepareManagerMessage(trainersSplit: List<Map<String, List<Trainer>>>, header: String): String {
    return "$header\n------------------\n<b>ИХ МОЖНО ПРИЗВАТЬ:</b>\n" + trainersSplit.joinToString("\n") { trainer ->
        trainer.values.first().first().name.escapeHTML() + ": " + trainer.toList().joinToString(" ") { "${it.first.escapeHTML()}: ${it.second.size}" }
    }
}

fun prepareBroadcastMessage(tableData: List<Map<String, String>>, header: String): String {
    return "<b>$header</b>\n\n" + tableData.mapIndexed { index, entry ->
        "${index + 1}.\n" + entry.toList().joinToString("\n") {
            "    <b>${it.first.escapeHTML()}:</b> ${it.second.escapeHTML()}"
        }
    }.joinToString("\n\n") { it }
}

fun prepareBroadcastMessageSingleField(tableData: List<Map<String, String>>, header: String): String {
    return "<b>$header: ${tableData.size}</b>\n" + tableData.map {
        it.toList().single()
    }.joinToString("\n") {
        "- ${it.first}: ${it.second}"
    }
}