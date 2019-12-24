package web

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import data.HBSHolder
import data.Trainer

val handlebars = Handlebars().also {
    it.registerHelper("inc", Helper<Int> { context, _ -> context + 1 })
}

fun splitByTrainersSegments(trainers: List<Trainer>): List<Map<String, List<Trainer>>> {
    val trainersByChatId = trainers.groupBy { it.chatId }.toList()

    return trainersByChatId.map { chatToUsers ->
        chatToUsers.second.groupBy { it.segment }
    }
}

fun prepareManagerData(trainersSplit: List<Map<String, List<Trainer>>>): Map<String, Map<String, Int>> {
    return trainersSplit.map { trainer ->
        trainer.values.first().first().name to trainer.toList().map { it.first to it.second.size }.toMap()
    }.toMap()
}

fun applyFromHbs(data: Any, rawTemplate: String, header: String?): String {
    val template = handlebars.compileInline(rawTemplate)

    val hbsData = HBSHolder(data, header)

    val context = Context
        .newBuilder(hbsData)
        .resolver(MethodValueResolver.INSTANCE, FieldValueResolver.INSTANCE)
        .build()

    return template.apply(context)
}

fun applyFromHbsFile(data: Any, filePath: String, header: String?): String {
    val template = handlebars.compile(filePath)

    val hbsData = HBSHolder(data, header)

    val context = Context
        .newBuilder(hbsData)
        .resolver(MethodValueResolver.INSTANCE, FieldValueResolver.INSTANCE)
        .build()

    return template.apply(context)
}