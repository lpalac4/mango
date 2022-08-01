package com.moraware.domain.usecase.user

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.UserRequest
import com.moraware.data.models.UserResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetUserUseCase: BaseUseCase<GetUser, GetUserFailure>() {
    private val callback = object : Callback<UserResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetUserFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: UserResponse) {
            val result = Either.Right(GetUser(UserMapper().transform(response)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = UserRequest()
        getRepository().retrieveUser(request, callback)
    }
}