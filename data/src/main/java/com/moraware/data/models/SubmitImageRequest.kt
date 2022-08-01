package com.moraware.data.models

import com.moraware.data.base.BaseRequest
import java.net.URI

class SubmitImageRequest(val path: URI = URI.create(""),
                         val userId: String = "",
                         val mealId: String = "",
                         val identifier: String = ""): BaseRequest() {
}