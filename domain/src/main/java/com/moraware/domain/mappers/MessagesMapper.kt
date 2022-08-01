package com.moraware.domain.mappers

import com.moraware.data.models.MessageThreadsResponse
import com.moraware.data.models.MessagesResponse
import com.moraware.data.models.SubmitMessageResponse
import com.moraware.domain.models.Message
import com.moraware.domain.models.MessageThread
import com.moraware.domain.usecase.messages.MessageThreads
import com.moraware.domain.usecase.messages.Messages
import com.moraware.domain.usecase.messages.SubmitMessage

class MessagesMapper {

    fun transform(response: MessagesResponse) : Messages {
        val messages = response.messages.let { msg ->
            msg.messages.map {
                Message(it.message, it.timestamp, it.senderId, it.senderName,
                        it.senderProfileImage, it.recipientId, it.recipientName, it.recipientProfileImage)
            }.toMutableList()
        }

        return Messages(messages)
    }

    fun transform(response: MessageThreadsResponse) : MessageThreads {
        val messageThreads = response.messageThreads.map {
            MessageThread(it.messageThreadIds, it.mealId, it.lastTimestamp,
                    it.senderId, it.senderName, it.senderProfile,
                    it.recipientId, it.recipientName, it.recipientProfile, it.mealName, it.messageThreadPreview)
        }.toMutableList()

        messageThreads.sortByDescending { it.lastTimestamp }

        return MessageThreads(messageThreads)
    }

    fun transform(response: SubmitMessageResponse): SubmitMessage {
        return SubmitMessage(response.timestamp)
    }
}