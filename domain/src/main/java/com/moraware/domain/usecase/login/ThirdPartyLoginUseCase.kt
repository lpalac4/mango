package com.moraware.domain.usecase.login

import com.moraware.domain.usecase.base.BaseUseCase

class ThirdPartyLoginUseCase(val uid: String,
                             val name: String,
                             val username: String,
                             val email: String,
                             val isChef: Boolean,
                             val latitude: Double,
                             val longitude: Double,
                             val city: String,
                             val zipcode: String,
                             val photo: String) : BaseUseCase<Login, LoginFailure>() {



    override suspend fun run() {

    }
}