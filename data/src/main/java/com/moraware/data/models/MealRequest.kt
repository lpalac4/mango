package com.moraware.data.models

import com.moraware.data.base.BaseRequest

data class MealRequest(val userId: String, val mealId: String? = null): BaseRequest() {
}