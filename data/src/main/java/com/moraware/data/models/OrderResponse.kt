package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MealEntity
import com.moraware.data.entities.OrderEntity

class OrderResponse(val mealEntity: MealEntity, val orderEntity: OrderEntity): BaseResponse() {
}