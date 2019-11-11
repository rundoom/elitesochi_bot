fun generateTrainerMessages(trainers: List<Trainer>): List<String> {
    val trainersByChatId = trainers.groupBy { it.chatId }.toList()
    val usersGrouped = trainersByChatId.map { chatToUsers ->
        val segmented = chatToUsers.second.groupBy { it.segment }.toList()
        segmented
    }
    println(usersGrouped)
    TODO()
}

fun formatStuckInDistribution(): String {
    return executeStuckInDistribution().joinToString("\n") {
        "${it.ruleName}: ${it.dealCount}"
    }
}