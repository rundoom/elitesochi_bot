import com.google.gson.JsonParser
import java.io.File

private val parser = JsonParser()
val configs = parser.parse(File("config.json").readText())
