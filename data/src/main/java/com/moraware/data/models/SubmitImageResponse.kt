package com.moraware.data.models

import com.moraware.data.base.BaseResponse

class SubmitImageResponse(val originalPath: String = "", val downloadUrl: String, val userId: String = "", val mealId: String = "") : BaseResponse() {
}