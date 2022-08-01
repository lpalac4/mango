package com.moraware.data.models

import com.moraware.data.base.BaseResponse
import java.net.URI

class ImageResponse(val mealId: Int, val url: URI) : BaseResponse() {
}