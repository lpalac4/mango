package com.moraware.domain.usecase.mymeals

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.MyChefMealsRequest
import com.moraware.data.models.MyMealsRequest
import com.moraware.data.models.MyMealsResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MealMapper
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class GetMyMealsUseCase(val patronId: String = "", val chefId: String = ""): BaseUseCase<MyMeals, GetMyMealsFailure>() {

    private val callback = object : Callback<MyMealsResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetMyMealsFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: MyMealsResponse) {
            if (patronId.isNotEmpty()) {
                val result = Either.Right(MyMeals(MealMapper().transform(response, patronId)))
                postToMainThread(result)
            } else if (chefId.isNotEmpty()) {
                val result = Either.Right(MyMeals(MealMapper().transform(response, chefId)))
                postToMainThread(result)
            }
        }
    }

    override suspend fun run() {
        if (patronId.isNotEmpty()) {
            mLogger?.log(Level.FINE, "Retrieving meals for patron: $patronId")
            getRepository().retrieveMyOrderedMeals(MyMealsRequest(patronId), callback)
        } else if (chefId.isNotEmpty()) {
            mLogger?.log(Level.FINE, "Retrieving meals for chefId: $chefId")
            getRepository().retrieveMyChefMeals(MyChefMealsRequest(chefId), callback)
        }
    }
}