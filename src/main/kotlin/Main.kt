import bot.initTelegramBot
import data.initExposedDB
import web.startServer

fun main() {
    initExposedDB()
    initTelegramBot()
    startServer()
}