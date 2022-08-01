package com.moraware.domain.mappers

import com.moraware.data.entities.UserEntity
import com.moraware.data.entities.UserThumbnailEntity
import com.moraware.data.models.LoginResponse
import com.moraware.data.models.RegisterUserResponse
import com.moraware.data.models.UserResponse
import com.moraware.domain.models.User
import com.moraware.domain.models.UserThumbnail

class UserMapper {
    fun transform(response: UserResponse): User {
        return transform(response.userEntity)
    }

    fun transform(response: RegisterUserResponse): User {
        return transform(response.userEntity)
    }

    fun transform(response: LoginResponse): User {
        return transform(response.userEntity)
    }

    fun transform(userEntity: UserEntity): User {
        return User(name = userEntity.name,
                email = userEntity.email,
                photoUrl = userEntity.photoUrl,
                id = userEntity.id,
                username = userEntity.username,
                chef = userEntity.chef,
                rating = userEntity.rating,
                address = userEntity.address,
                city = userEntity.city,
                state = userEntity.state,
                zipCode = userEntity.zipCode,
                thirdPartyToken = userEntity.thirdPartyToken,
                followers = userEntity.followers,
                following = userEntity.following,
                cookedMeals = userEntity.cookedMeals,
                orderedMeals = userEntity.orderedMeals,
                notificationToken = userEntity.notificationTokens,
                bio = userEntity.bio)
    }

    fun transform(userEntity: UserThumbnailEntity): UserThumbnail {
        return UserThumbnail(userEntity.id,
                userEntity.photoUrl,
                userEntity.username)
    }
}