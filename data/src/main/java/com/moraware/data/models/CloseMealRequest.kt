package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class CloseMealRequest(val mealId: String, val chefId: String, val forceDelete: Boolean = false): BaseRequest() {
}