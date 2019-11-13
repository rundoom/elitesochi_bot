package web

import data.Trainer
import data.getStuckInDistribution


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
        it.first to "$header\n------------------\n*ИХ МОЖНО ПРИЗВАТЬ:*${it.second}\n$header"
    }
}

fun formatStuckInDistribution(): String {
    return "*ЗАВИСЛИ В РАЗДАЧЕ:*\n" + getStuckInDistribution().joinToString("\n") {
        "${it.ruleName}: ${it.dealCount}"
    }
}

fun prepareManagerMessage(trainersSplit: List<Map<String, List<Trainer>>>, header: String): String {
    return "$header\n------------------\n*ИХ МОЖНО ПРИЗВАТЬ:*\n" + trainersSplit.joinToString("\n") { trainer ->
        trainer.values.first().first().name + ": " + trainer.toList().joinToString(" ") { "${it.first}: ${it.second.size}" }
    }
}