package com.moraware.domain.usecase.profile

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.UserThumbnail

class SocialNetwork(val id: String, val network: MutableList<UserThumbnail>): DomainResponse()