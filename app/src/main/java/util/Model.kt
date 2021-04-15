package util

data class User(
    val name: String? = "",
    val phone: String? = "",
    val email: String? = "",
//    val password: String? = "",
    val imageUrl: String? = "",
    val status: String? = "",
    val statusUrl: String? = "",
    val statusTime: String? = ""
)

data class Contact(
    val name: String?,
    val phone: String?
)

data class Chat(
    val chatParticipants: ArrayList<String>
)

data class Message(
    val sentby: String? = "",
    val message: String? = "",
    val messageTime: Long? = 0
)

data class StatusListElement(
    val userName: String?,
    val userUrl: String?,
    val status: String?,
    val statusUrl: String?,
    var statusTime: String?
)