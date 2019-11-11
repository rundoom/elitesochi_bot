import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect(
        "jdbc:sqlserver://195.123.175.230:11433",
        user = "telegram_bot",
        password = "TS^gvS^StS8S8HS5SZ6tzxgx66ss6",
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    )
//    transaction {
//        connection.createStatement().execute("{call [AnalyticData].[dbo].[p_telegram_trainers_stops]}")
//    }
//    println()

    transaction {
        val settings = Settings.selectAll()
        val deals = Deals.selectAll()
        println(settings)
        println(deals)
    }
}

object Settings : Table("AnalyticData.dbo.bot_configuration") {
    val name = varchar("NAME", length = 64) // Column<String>
    val value = varchar("VALUE", length = 256) // Column<String>
}

object Deals : Table("AnalyticData.dbo.bot_rule_razdachi"){
    val dealCount = integer("DEALS")
    val ruleName = varchar("RULE", length = 64)
}

inline fun <reified T>oioi(){

}