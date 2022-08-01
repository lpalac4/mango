package com.moraware.domain.usecase.messages

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.MessageThread

data class MessageThreads(val messageThreads: MutableList<MessageThread>): DomainResponse() {

}