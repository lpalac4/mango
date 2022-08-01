package com.moraware.data.mappers

import com.moraware.data.entities.UserEntity

class UserEntityMapper {

    fun toUserEntity(firebaseAuthId: String, name: String, username: String, email: String,
                     photoUrl: String, isChef: Boolean) : UserEntity {
        return UserEntity(firebaseAuthId, name, username, email, photoUrl, isChef)
    }
}