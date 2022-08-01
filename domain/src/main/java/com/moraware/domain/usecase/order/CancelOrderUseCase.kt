package com.moraware.domain.usecase.order

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.CancelOrderRequest
import com.moraware.data.models.CancelOrderResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class CancelOrderUseCase(val orderId: String, val mealId: String, val patronId: String, val numberOfOrders: Int) : BaseUseCase<CancelOrder, CancelOrderFailure>() {

    private val callback = object : Callback<CancelOrderResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(CancelOrderFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: CancelOrderResponse) {
            val result = Either.Right(CancelOrder(response.orderId, response.patronId, response.numberOfOrders))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Cancelling order: $orderId")
        getRepository().cancelOrder(CancelOrderRequest(orderId, mealId, patronId, numberOfOrders), callback)
    }
}