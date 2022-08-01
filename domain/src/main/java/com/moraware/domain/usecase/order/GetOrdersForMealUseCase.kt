package com.moraware.domain.usecase.order

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.RetrieveOrdersForMealRequest
import com.moraware.data.models.RetrieveOrdersForMealResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.OrderMapper
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class GetOrdersForMealUseCase(val mealId: String): BaseUseCase<OrdersForMeal, GetOrdersForMealFailure>() {

    private val callback = object : Callback<RetrieveOrdersForMealResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(GetOrdersForMealFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: RetrieveOrdersForMealResponse) {
            val result = Either.Right(OrderMapper().transform(response))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Retrieving meals for patron: $mealId")
        getRepository().retrieveOrdersForMeal(RetrieveOrdersForMealRequest(mealId), callback)
    }
}