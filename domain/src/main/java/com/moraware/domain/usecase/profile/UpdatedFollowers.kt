package com.moraware.domain.usecase.profile

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.UserThumbnail

data class UpdatedFollowers(val deleted: Boolean, val updateFollower: UserThumbnail): DomainResponse()