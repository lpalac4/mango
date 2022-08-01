package com.moraware.mango.firebase.mappers

import com.google.firebase.auth.FirebaseUser
import com.moraware.data.entities.UserEntity
import com.moraware.data.models.CredentialsRegisterUserRequest

class FirebaseUserMapper {
    fun toUserEntity(user: FirebaseUser): UserEntity {
        return UserEntity(
                id = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                username = "",
                photoUrl = user.photoUrl.toString(),
                chef = false)
    }

    fun toUserEntity(user: FirebaseUser, request: CredentialsRegisterUserRequest): UserEntity {
        val fullName = if(request.firstName?.isNotEmpty() == true
                && request.lastName?.isNotEmpty() == true) "${request.firstName} ${request.lastName}" else ""

        return UserEntity(
                id = user.uid,
                name = fullName,
                username = request.username,
                email = request.email,
                photoUrl = user.photoUrl.toString(),
                chef = request.isChef(),
                rating = 0,
                address = request.address ?: "",
                city = request.city ?: "",
                state = request.state ?: "",
                zipCode = request.zipcode ?: ""
                )
    }
}