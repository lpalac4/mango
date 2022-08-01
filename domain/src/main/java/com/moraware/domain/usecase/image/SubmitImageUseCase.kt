package com.moraware.domain.usecase.image

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.SubmitImageRequest
import com.moraware.data.models.SubmitImageResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.ImageMapper
import com.moraware.domain.usecase.base.BaseUseCase
import com.moraware.domain.usecase.user.GetUser
import java.net.URI

class SubmitImageUseCase(val user: GetUser, val imageUri: URI): BaseUseCase<Image, SubmitImageFailure>() {

    private val callback = object : Callback<SubmitImageResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(SubmitImageFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: SubmitImageResponse) {
            val result = Either.Right(ImageMapper().transform(response))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        var request = SubmitImageRequest(path = imageUri)
    }
}