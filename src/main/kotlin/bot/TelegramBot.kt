package bot

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.string
import configs
import data.executeEmailSending
import data.executeTrainersStop
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.ParseMode
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.Proxy


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
        command("force_email") { bot, update ->
            executeEmailSending()
            bot.sendMessage(chatId = update.message!!.chat.id, text = "Сообщения разосланы на электронную почту")
        }
        command("force_tg_send") { bot, update ->
            executeTrainersStop()
            bot.sendMessage(chatId = update.message!!.chat.id, text = "Сообщения разосланы в Телеграм")
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