package com.ifcompany.usedtrading.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
) {

    constructor(): this("", "")
}
