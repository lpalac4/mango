package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MealEntity

class LocalMealsResponse(val mealEntities: List<MealEntity>) : BaseResponse() {
}