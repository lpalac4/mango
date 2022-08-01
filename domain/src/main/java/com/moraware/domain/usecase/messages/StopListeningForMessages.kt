package com.moraware.domain.usecase.messages

import com.moraware.data.base.BaseResponse
import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase

class StopListeningForMessages: BaseUseCase<StopListening, StopListeningFailure>() {

    private val callback = object : Callback<BaseResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(StopListeningFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: BaseResponse) {
            val result = Either.Right(StopListening())
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        try {
            getRepository().stopListeningForMessages(callback)
            GetMessagesUseCase.clearChannel()
        } catch (e: Exception) {
            // TODO: Figure out what to do here
        }
    }
}