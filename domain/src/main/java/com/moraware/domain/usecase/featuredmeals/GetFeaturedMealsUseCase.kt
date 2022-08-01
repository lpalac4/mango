package com.moraware.domain.usecase.featuredmeals

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.FeaturedMealsRequest
import com.moraware.data.models.FeaturedMealsResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MealMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetFeaturedMealsUseCase(val zipcode: String, val userId: String) : BaseUseCase<FeaturedMeals, GetFeaturedMealsFailure>() {
    private val callback = object : Callback<FeaturedMealsResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetFeaturedMealsFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: FeaturedMealsResponse) {
            val result = Either.Right(FeaturedMeals(MealMapper().transform(response, userId)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = FeaturedMealsRequest(zipcode)
        getRepository().retrieveFeaturedMeals(request, callback)
    }
}