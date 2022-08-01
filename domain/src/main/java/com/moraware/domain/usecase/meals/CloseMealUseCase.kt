package com.moraware.domain.usecase.meals

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.CloseMealRequest
import com.moraware.data.models.CloseMealResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.*
import java.util.logging.Level

class CloseMealUseCase(val mealId: String, val chefId: String, val mealETA: Date, val forceDelete: Boolean = false): BaseUseCase<CloseMeal, CloseMealFailure>() {
    private val callback = object : Callback<CloseMealResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(CloseMealFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: CloseMealResponse) {
            val result = Either.Right(CloseMeal())
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        if(mealETA.after(Date()) && !forceDelete) {
            callback.onFailure(WebServiceException(null))
            return
        }

        mLogger?.log(Level.FINE, "Closing meal: $mealId")
        val request = CloseMealRequest(mealId, chefId)

        getRepository().closeMeal(request, callback)
    }
}