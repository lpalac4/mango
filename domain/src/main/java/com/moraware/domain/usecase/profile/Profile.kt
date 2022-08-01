package com.moraware.domain.usecase.profile

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.User

class Profile(val user: User) : DomainResponse()