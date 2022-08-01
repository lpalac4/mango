package com.moraware.domain.usecase.login

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.AnonymousRegisterUserRequest
import com.moraware.data.models.RegisterUserResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase

class AnonymousLoginUseCase : BaseUseCase<Login, LoginFailure>() {
    private val callback = object : Callback<RegisterUserResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(LoginFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: RegisterUserResponse) {
            val result = Either.Right(Login(UserMapper().transform(response)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = AnonymousRegisterUserRequest()
        getRepository().registerUser(request, callback)
    }
}