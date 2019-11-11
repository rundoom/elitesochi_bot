import com.google.gson.annotations.SerializedName

data class Trainer(
    @SerializedName("TRAINER_NAME") val name: String,
    @SerializedName("CHAT_ID") val chatId: String,
    @SerializedName("USER_NAME") val username: String,
    @SerializedName("GOT") val got: Int,
    @SerializedName("USER_SEGMENT") val segment: String,
    @SerializedName("PHONE") val phone: String
)