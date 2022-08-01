package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MessageThreadEntity

class MessageThreadsResponse(val messageThreads: List<MessageThreadEntity>) : BaseResponse() {
}