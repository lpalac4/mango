package com.moraware.data.models

import com.moraware.data.base.BaseResponse

class OrderConfirmResponse(val orderId: String, val acceptedStatus: Boolean, val numberOfOrders: Int) : BaseResponse() {
}