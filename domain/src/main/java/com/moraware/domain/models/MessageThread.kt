package com.moraware.domain.models

data class MessageThread(val messageThreadIds: List<String>,
                         val mealId: String,
                         val lastTimestamp: Long,
                         val senderId: String,
                         val senderName: String,
                         val senderProfile: String,
                         val recipientId: String,
                         val recipientName: String,
                         val recipientProfile: String,
                         val mealName: String,
                         val messageThreadPreview: String) {
}