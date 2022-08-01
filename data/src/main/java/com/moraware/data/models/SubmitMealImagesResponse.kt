package com.moraware.data.models

import com.moraware.data.base.BaseResponse

class SubmitMealImagesResponse(val downloadUrls: List<String>, val featuredUrl: String) : BaseResponse() {
}