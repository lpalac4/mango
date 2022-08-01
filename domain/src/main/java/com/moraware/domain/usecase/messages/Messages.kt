package com.moraware.domain.usecase.messages

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Message

data class Messages(val messages: MutableList<Message>): DomainResponse() {

}