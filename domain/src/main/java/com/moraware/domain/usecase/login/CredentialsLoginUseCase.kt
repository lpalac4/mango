package com.moraware.domain.usecase.login

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.LoginRequest
import com.moraware.data.models.LoginResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase

class CredentialsLoginUseCase(val email: String, val password: String = "", val thirdPartyId: String = "") : BaseUseCase<Login, LoginFailure>() {

    private val callback = object : Callback<LoginResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(LoginFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: LoginResponse) {
            val result = Either.Right(Login(UserMapper().transform(response)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = LoginRequest(email, password, thirdPartyId)
        getRepository().loginUser(request, callback)
    }
}