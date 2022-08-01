package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class OrderRequest(val mealId: String, val numberOrders: Int, val patronId: String, val patronName: String): BaseRequest() {
}