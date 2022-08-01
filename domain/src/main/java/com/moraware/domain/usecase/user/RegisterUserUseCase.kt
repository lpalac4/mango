package com.moraware.domain.usecase.user

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.*
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase
import java.net.URI

class RegisterUserUseCase(val userName: String, val email: String, val password: String,
                          val firstName: String?, val lastName: String?, val address: String?,
                          val city: String?, val state: String?, val zipcode: String?, val phone: String?, val imageURI: URI?) : BaseUseCase<GetUser, RegisterUserFailure>() {

    constructor(userName: String, email: String, password: String, imageURI: URI?)
            : this(userName, email, password, null, null, null, null, null, null, null, imageURI)

    private val callback = object : Callback<RegisterUserResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(RegisterUserFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: RegisterUserResponse) {
            postToMainThread(Either.Right(GetUser(UserMapper().transform(response))))
        }
    }

    override suspend fun run() {
        val request = CredentialsRegisterUserRequest(userName, email, password, firstName, lastName, address,
                city, state, zipcode, phone, imageURI)
        getRepository().registerUser(request, callback)
    }
}