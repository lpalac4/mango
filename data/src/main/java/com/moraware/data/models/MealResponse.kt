package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MealEntity

data class MealResponse(val mealEntity: MutableList<MealEntity>): BaseResponse() {
}