package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.UserEntity

class RegisterUserResponse(val userEntity: UserEntity) : BaseResponse() {
}