package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import com.moraware.data.entities.MealEntity

class SubmitMealResponse(val meal: MealEntity): BaseResponse() {
}