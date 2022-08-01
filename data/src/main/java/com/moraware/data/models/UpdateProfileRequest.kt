package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class UpdateProfileRequest(val id: String, val bio: String?, val notificationEnabled: Boolean?, val notificationTokensToAdd: MutableList<String>?) : BaseRequest()