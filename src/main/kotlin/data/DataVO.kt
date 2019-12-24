package data

import com.google.gson.annotations.SerializedName

data class Trainer(
    @SerializedName("TRAINER_NAME") val name: String,
    @SerializedName("CHAT_ID") val chatId: Long,
    @SerializedName("USER_NAME") val username: String,
    @SerializedName("GOT") val got: Int,
    @SerializedName("USER_SEGMENT") val segment: String,
    @SerializedName("PHONE") val phone: String
)

data class HBSHolder(val data: Any, val header: String? = null)