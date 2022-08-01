package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class ChangePasswordRequest(val email: String, val oldPassword: String, val newPassword: String) : BaseRequest() {
}