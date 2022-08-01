package com.moraware.domain.usecase.messages

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.SubmitMessageRequest
import com.moraware.data.models.SubmitMessageResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MessagesMapper
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class SubmitMessageUseCase(val mealId: String = "",
                           val firstUserId: String = "",
                           val firstUserName: String = "",
                           val firstUserProfile: String = "",
                           val secondUserId: String = "",
                           val secondUserName: String = "",
                           val secondUserProfile: String = "",
                           val messageThreadName: String = "",
                           val message: String,
                           val timestamp: Long): BaseUseCase<SubmitMessage, SubmitMessageFailure>() {

    private val callback = object : Callback<SubmitMessageResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(SubmitMessageFailure(timestamp))
            postToMainThread(result)
        }

        override fun onSuccess(response: SubmitMessageResponse) {
            val result = Either.Right(MessagesMapper().transform(response))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Retrieving messages from: $firstUserId to: $secondUserId")
        val request = SubmitMessageRequest(mealId, firstUserId, firstUserName, firstUserProfile, secondUserId,
                secondUserName, secondUserProfile, messageThreadName, message, timestamp)

        getRepository().submitMessage(request, callback)
    }
}