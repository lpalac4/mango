package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MealEntity

class MyMealsResponse(val mealEntities: List<MealEntity>) : BaseResponse() {
}