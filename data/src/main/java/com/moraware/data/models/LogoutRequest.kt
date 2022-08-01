package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class LogoutRequest(val username: String, val id: String, val thirdPartyId: String): BaseRequest() {

}