package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class CancelOrderRequest(val orderId: String, val mealId: String, val patronId: String,
                         val numberOfOrders: Int): BaseRequest() {
}