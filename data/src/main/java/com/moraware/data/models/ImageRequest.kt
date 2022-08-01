package com.moraware.data.models

import com.moraware.data.base.BaseRequest
import java.net.URI

class ImageRequest: BaseRequest() {
    @JvmField
    var mealId: Int = 0
    @JvmField
    var url: URI = URI.create("")
}