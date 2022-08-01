package com.moraware.data.models

import com.moraware.data.base.BaseRequest

open class MessagesRequest(var mealId: String, var firstUser: String, var secondUser: String): BaseRequest() {
}