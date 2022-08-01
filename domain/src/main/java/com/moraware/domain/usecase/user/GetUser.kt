package com.moraware.domain.usecase.user

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.User

class GetUser(val user: User) : DomainResponse()