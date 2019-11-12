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
    token = "999140102:AAGEEMIX9P0udqdGGFwNopaLCeXJxdSuJHY"
    proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("10.10.1.91", 9050))
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

fun sendBotMessage(chatId: Long, message: String){
    bot.sendMessage(chatId, message, ParseMode.MARKDOWN)
}