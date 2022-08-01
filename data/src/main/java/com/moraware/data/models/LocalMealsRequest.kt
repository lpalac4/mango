package com.moraware.data.models

import com.moraware.data.base.BaseRequest

class LocalMealsRequest(val northeast: DoubleArray, val southwest: DoubleArray) : BaseRequest() {
}