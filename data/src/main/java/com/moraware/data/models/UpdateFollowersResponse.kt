package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.UserThumbnailEntity

class UpdateFollowersResponse(val deleted: Boolean, val followerUpdated: UserThumbnailEntity): BaseResponse() {
}