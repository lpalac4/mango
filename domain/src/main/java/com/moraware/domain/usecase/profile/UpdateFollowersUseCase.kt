package com.moraware.domain.usecase.profile

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.UpdateFollowersRequest
import com.moraware.data.models.UpdateFollowersResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.UserMapper
import com.moraware.domain.usecase.base.BaseUseCase

class UpdateFollowersUseCase(val delete: Boolean, val userId: String, val userPhotoUrl: String,
                             val username: String, val otherUserId: String, val otherUserPhotoUrl: String,
                             val otherUsername: String): BaseUseCase<UpdatedFollowers, UpdateFollowersFailure>() {

    val callback = object : Callback<UpdateFollowersResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(UpdateFollowersFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: UpdateFollowersResponse) {
            val result = Either.Right(UpdatedFollowers(response.deleted, UserMapper().transform(response.followerUpdated)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = UpdateFollowersRequest(delete, userId, userPhotoUrl, username, otherUserId, otherUserPhotoUrl, otherUsername)
        getRepository().updateFollowers(request, callback)
    }
}