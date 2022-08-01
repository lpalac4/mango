package com.moraware.domain.usecase.meals

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.MealRequest
import com.moraware.data.models.MealResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MealMapper
import com.moraware.domain.usecase.base.BaseUseCase

class GetMealsUseCase(val userId: String = "", val mealId: String? = null) : BaseUseCase<GetMeals, GetMealsFailure>() {
    val callback = object : Callback<MealResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetMealsFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: MealResponse) {
            val result = Either.Right(GetMeals(MealMapper().transform(response.mealEntity, userId)))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        val request = MealRequest(userId, mealId)
        getRepository().retrieveMeal(request, callback)
    }
}