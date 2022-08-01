package com.moraware.domain.usecase.profile

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.ProfileResponse
import com.moraware.data.models.UpdateProfileRequest
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase

class UpdateProfileUseCase(val userId: String, val bio: String? = null, val notificationEnabled: Boolean? = null, val notificationTokensToAdd: MutableList<String>? = null)
    : BaseUseCase<Profile, UpdateProfileFailure>() {

    val callback = object : Callback<ProfileResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(UpdateProfileFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: ProfileResponse) {
            val result = Either.Right(Profile(UserMapper().transform(response.userEntity)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = UpdateProfileRequest(userId, bio = bio, notificationEnabled = notificationEnabled, notificationTokensToAdd = notificationTokensToAdd)
        getRepository().updateProfile(request, callback)
    }
}