package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class ProfileRequest(val currentUserId: String, val id: String): BaseRequest()