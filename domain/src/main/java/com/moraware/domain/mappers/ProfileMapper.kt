package com.moraware.domain.mappers

import com.moraware.data.models.FollowersResponse
import com.moraware.data.models.ProfileResponse
import com.moraware.domain.models.UserThumbnail
import com.moraware.domain.usecase.profile.SocialNetwork
import com.moraware.domain.usecase.profile.Profile

class ProfileMapper {
    fun transform(response: ProfileResponse): Profile {
        return Profile(UserMapper().transform(response.userEntity))
    }

    fun transform(response: FollowersResponse): SocialNetwork {
        var followers = mutableListOf<UserThumbnail>()
        var userId = ""

        response.socialNetworkEntities.forEach {
            followers.add(UserThumbnail(it.recipient, it.photoUrl, it.username))
            if(userId.isEmpty()) userId = it.originator
        }

        return SocialNetwork(userId, followers)
    }

}