package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MessagesEntity

data class MessagesResponse(val messages: MessagesEntity): BaseResponse() {

}