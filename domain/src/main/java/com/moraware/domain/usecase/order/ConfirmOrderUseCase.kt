package com.moraware.domain.usecase.order

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.OrderConfirmRequest
import com.moraware.data.models.OrderConfirmResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class ConfirmOrderUseCase(val orderId: String, val mealId: String, val patronId: String, val numberOfOrders: Int, private val currentAcceptedStatus: Boolean) : BaseUseCase<ConfirmOrder, ConfirmOrderFailure>() {

    private val callback = object : Callback<OrderConfirmResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(ConfirmOrderFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: OrderConfirmResponse) {
            val result = Either.Right(ConfirmOrder(response.orderId, response.acceptedStatus, response.numberOfOrders))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Retrieving meals for patron: $mealId")
        if(currentAcceptedStatus) callback.onFailure(WebServiceException(null)) // request already confirmed
        else getRepository().confirmOrder(OrderConfirmRequest(orderId, mealId, patronId, numberOfOrders), callback)
    }
}