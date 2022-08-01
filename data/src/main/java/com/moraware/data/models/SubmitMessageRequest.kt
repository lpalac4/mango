package com.moraware.data.models

import com.moraware.data.base.BaseRequest

data class SubmitMessageRequest(val mealId: String,
                                val senderUserId: String, val senderUserName: String, val senderUserProfile: String,
                                val recipientUserId: String, val recipientUserName: String, val recipientUserProfile: String,
                                val mealName: String, val message: String, val timestamp: Long): BaseRequest() {
}