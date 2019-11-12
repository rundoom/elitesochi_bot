package web

import Trainer
import getStuckInDistribution

fun generateTrainerMessagesBody(trainers: List<Trainer>): List<Pair<Long, String>> {
    val trainersByChatId = trainers.groupBy { it.chatId }.toList()

    return trainersByChatId.map { chatToUsers ->
        val segmented = chatToUsers.second.groupBy { it.segment }.toList()

        chatToUsers.first to segmented.joinToString("\n") { segment ->
            "------------------\n${segment.first}:\n" +
                    segment.second.joinToString("\n\n") { "${it.username}\n${it.phone}" }
        }
    }
}

fun prepareTrainerFullMessages(bodyMessages: List<Pair<Long, String>>, header: String): List<Pair<Long, String>> {
    return bodyMessages.map {
        it.first to "$header\n${it.second}\n------------------\n$header"
    }
}

fun formatStuckInDistribution(): String {
    return "*ЗАВИСЛИ В РАЗДАЧЕ:*\n" + getStuckInDistribution().joinToString("\n") {
        "${it.ruleName}: ${it.dealCount}"
    }
}