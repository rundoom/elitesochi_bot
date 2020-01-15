package bot

import com.github.salomonbrys.kotson.*
import configs
import data.executeProcedureFromList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.callbackQuery
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.InlineKeyboardButton
import me.ivmg.telegram.entities.InlineKeyboardMarkup
import me.ivmg.telegram.entities.ParseMode
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.Proxy

const val procedurePrefix = "procedure_"

val bot = bot {
    token = configs["bot"]["token"].string

    proxy = if (!configs["bot"]["proxy"].isJsonNull) Proxy(
        Proxy.Type.SOCKS,
        InetSocketAddress(configs["bot"]["proxy"]["host"].string, configs["bot"]["proxy"]["port"].int)
    ) else Proxy.NO_PROXY

    logLevel = HttpLoggingInterceptor.Level.NONE

    dispatch {
        command("my_chat_id") { bot, update ->
            bot.sendMessage(chatId = update.message!!.chat.id, text = update.message!!.chat.id.toString())
        }

        command("procedures") { bot, update ->
            if (update.message!!.chat.id !in configs["bot"]["manager_list"].array.map { it.long }) {
                bot.sendMessage(
                    chatId = update.message!!.chat.id,
                    text = "Только пользователи из списка менеджеров могут исполнять процедуры"
                )
                return@command
            }

            val inlineKeyboardMarkup = InlineKeyboardMarkup(generateButtons())
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = "Доступные команды:",
                replyMarkup = inlineKeyboardMarkup
            )
        }

        callbackQuery(procedurePrefix) { bot, update ->
            GlobalScope.launch {
                update.callbackQuery?.let {
                    val chatId = it.message?.chat?.id ?: return@launch
                    executeProcedureFromList(it.data.substringAfter(procedurePrefix))
                    bot.sendMessage(chatId = chatId, text = "Процедура ${it.data} исполнена")
                }
            }
        }
    }
}

fun initTelegramBot() {
    GlobalScope.launch { bot.startPolling() }
}

fun sendBotMessage(
    chatId: Long,
    message: String,
    parseMode: ParseMode? = ParseMode.MARKDOWN,
    disableWebPagePreview: Boolean = false
) {
    bot.sendMessage(chatId, message, parseMode, disableWebPagePreview = disableWebPagePreview)
}

fun generateButtons(): List<List<InlineKeyboardButton>> {
    return configs["bot"]["procedures"].array.map {
        listOf(
            InlineKeyboardButton(
                text = it["name"].string,
                callbackData = "$procedurePrefix${it["mnemonic"].string}"
            )
        )
    }
}