package com.moraware.domain.usecase.zipcodes

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.LocalMealsRequest
import com.moraware.data.models.LocalMealsResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MealMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetNearbyMealsByBoundaries(val northeast: DoubleArray, val southwest: DoubleArray, val userId: String)
    : BaseUseCase<NearbyMeals, GetNearbyMealsFailure>() {

    private val callback = object : Callback<LocalMealsResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetNearbyMealsFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: LocalMealsResponse) {
            val result = Either.Right(NearbyMeals(MealMapper().transform(response, userId)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = LocalMealsRequest(northeast, southwest)
        getRepository().retrieveLocalMeals(request, callback)
    }
}