package com.moraware.domain.usecase.logout

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.LogoutRequest
import com.moraware.data.models.LogoutResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase

class LogoutUseCase(val username: String, val userId: String, val thirdPartyId: String): BaseUseCase<Logout, LogoutFailure>() {

    private val callback = object : Callback<LogoutResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(LogoutFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: LogoutResponse) {
            val result = Either.Right(Logout())
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = LogoutRequest(username, userId, thirdPartyId)
        getRepository().logoutUser(request, callback)
    }

}