package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.SocialNetworkEntity

class FollowersResponse(val socialNetworkEntities: MutableList<SocialNetworkEntity>) : BaseResponse()