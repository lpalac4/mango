package com.moraware.data.entities

data class MessagesEntity(var messageIds: List<String> = emptyList(),
                          val mealId: String = "",
                          var messages: List<MessageEntity> = emptyList(),
                          var timestamp: Long = 0)