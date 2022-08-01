package com.moraware.data.models

import com.moraware.data.base.BaseResponse

class CancelOrderResponse(val orderId: String, val patronId: String, val numberOfOrders: Int): BaseResponse() {
}