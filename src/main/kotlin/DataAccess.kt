import Deals.dealCount
import Deals.ruleName
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun initExposedDB() {
    Database.connect(
        "jdbc:sqlserver://195.123.175.230:11433",
        user = "telegram_bot",
        password = "TS^gvS^StS8S8HS5SZ6tzxgx66ss6",
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    )
}

fun getStuckInDistribution(): List<Deal> {
    return transaction {
        Deals.selectAll().map {
            Deal(it[dealCount], it[ruleName])
        }
    }
}

fun executeTrainersStop() {
    transaction {
        connection.createStatement().execute("{call [AnalyticData].[dbo].[p_telegram_trainers_stops]}")
    }
}

fun executeEmailSending() {
    transaction {
        connection.createStatement().execute("{call [AnalyticData].[dbo].[p_newbuilding_need_update_email]}")
    }
}

object Settings : Table("AnalyticData.dbo.bot_configuration") {
    val name = varchar("NAME", length = 64) // Column<String>
    val value = varchar("VALUE", length = 256) // Column<String>
}

object Deals : Table("AnalyticData.dbo.bot_rule_razdachi") {
    val dealCount = integer("DEALS")
    val ruleName = varchar("RULE", length = 64)
}

data class Deal(val dealCount: Int, val ruleName: String)