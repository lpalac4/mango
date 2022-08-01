package com.moraware.domain.usecase.user

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.ChangePasswordRequest
import com.moraware.data.models.ChangePasswordResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase

class ChangePasswordUseCase(val email: String, val oldPassword: String, val newPassword: String) : BaseUseCase<ChangePassword, ChangePasswordFailure>() {

    private val callback = object : Callback<ChangePasswordResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(ChangePasswordFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: ChangePasswordResponse) {
            val result = Either.Right(ChangePassword())
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = ChangePasswordRequest(email, oldPassword, newPassword)
        getRepository().changePassword(request, callback)
    }
}