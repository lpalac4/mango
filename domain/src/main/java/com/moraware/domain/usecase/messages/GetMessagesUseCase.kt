package com.moraware.domain.usecase.messages

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Channel
import com.moraware.data.models.MessagesRequest
import com.moraware.data.models.MessagesResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MessagesMapper
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

open class GetMessagesUseCase(var mealId: String = "", var firstUser: String = "", var secondUser: String = "") : BaseUseCase<Messages, GetMessagesFailure>() {

    companion object {
        private var channel: Channel<MessagesResponse>? = null
        fun clearChannel() {
            channel = null
        }
    }

    protected fun getChannel(): Channel<MessagesResponse> {
        if (channel == null) {
            channel = object : Channel<MessagesResponse> {
                override fun onFailure(exception: WebServiceException) {
                    val result = Either.Left(GetMessagesFailure())
                    postToMainThread(result)
                }

                override fun onUpdate(response: MessagesResponse) {
                    val result = Either.Right(MessagesMapper().transform(response))
                    postToMainThread(result)
                }
            }
        }

        return channel!!
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Retrieving messages for $mealId from: $firstUser to: $secondUser")
        val request = MessagesRequest(mealId, firstUser, secondUser)

        getRepository().listenForMessages(request, getChannel())
    }
}