package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.UserEntity

class LoginResponse(val userEntity: UserEntity) : BaseResponse() {
}