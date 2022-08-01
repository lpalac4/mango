package com.moraware.domain.usecase.profile

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.ProfileRequest
import com.moraware.data.models.ProfileResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.ProfileMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetProfileUseCase(val currentUserId: String, val userId: String): BaseUseCase<Profile, GetProfileFailure>() {

    private val callback = object : Callback<ProfileResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetProfileFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: ProfileResponse) {
            val result = Either.Right(ProfileMapper().transform(response))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = ProfileRequest(currentUserId, userId)
        getRepository().retrieveProfile(request, callback)
    }
}