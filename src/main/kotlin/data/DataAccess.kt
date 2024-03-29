package data

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import configs
import data.Deals.dealCount
import data.Deals.ruleName
import data.Settings.name
import data.Settings.value
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun initExposedDB() {
    Database.connect(
        url = configs["data"]["jdbc_url"].string,
        user = configs["data"]["user"].string,
        password = configs["data"]["password"].string,
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

fun executeProcedureFromList(mnemonic: String) {
    val procedureCommand =
        configs["bot"]["procedures"].array.find { it["mnemonic"].string == mnemonic }!!["procedure"].string

    transaction { connection.createStatement().execute("{call $procedureCommand}") }
}

fun getSettings(): List<Setting> {
    return transaction {
        Settings.selectAll().map {
            Setting(it[name], it[value])
        }
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
data class Setting(val name: String, val value: String)