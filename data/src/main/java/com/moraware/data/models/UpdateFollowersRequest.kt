package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class UpdateFollowersRequest(val delete: Boolean, val id: String, val photoUrl: String, val username: String, val followerId: String, val followerPhotoUrl: String, val followerUsername: String): BaseRequest() {
}