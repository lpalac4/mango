package com.moraware.domain.mappers

import com.moraware.data.models.SubmitImageResponse
import com.moraware.domain.usecase.image.Image

class ImageMapper {
    fun transform(response: SubmitImageResponse): Image {
        return Image()
    }
}