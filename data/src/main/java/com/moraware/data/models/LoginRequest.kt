package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class LoginRequest(val email: String, val password: String, val thirdPartyId: String) : BaseRequest() {
}