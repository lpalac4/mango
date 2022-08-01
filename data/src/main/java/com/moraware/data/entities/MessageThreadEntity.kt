package com.moraware.data.entities

data class MessageThreadEntity(val participants: List<String> = emptyList(),
                               val messageThreadIds: List<String> = emptyList(),
                               val mealId: String = "",
                               val lastTimestamp: Long = 0,
                               val senderId: String = "",
                               val senderName: String = "",
                               val senderProfile: String = "",
                               val recipientId: String = "",
                               val recipientName: String = "",
                               val recipientProfile: String = "",
                               val mealName: String = "",
                               val messageThreadPreview: String = "") {
}