package com.moraware.domain.models

data class Message(var message: String,
                   var timestamp: Long,
                   var senderId: String,
                   var senderName: String,
                   var senderProfileImage: String,
                   var recipientId: String,
                   var recipientName: String,
                   var recipientProfileImage: String) {

    var error: Boolean = false
    var uploaded: Boolean = true
}