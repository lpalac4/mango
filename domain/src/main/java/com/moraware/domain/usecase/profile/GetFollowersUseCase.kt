package com.moraware.domain.usecase.profile

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.FollowersRequest
import com.moraware.data.models.FollowersResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.ProfileMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetFollowersUseCase(val userId: String): BaseUseCase<SocialNetwork, GetFollowersFailure>() {

    val callback = object : Callback<FollowersResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetFollowersFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: FollowersResponse) {
            val result = Either.Right(ProfileMapper().transform(response))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = FollowersRequest(userId)
        getRepository().retrieveFollowers(request, callback)
    }
}