package com.moraware.domain.usecase.order

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.OrderRequest
import com.moraware.data.models.OrderResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.models.Order
import com.moraware.domain.usecase.base.BaseUseCase
import java.util.logging.Level

class SubmitOrderUseCase(val mealId: String = "", val numberOrders: Int, val patronId: String, val patronName: String) : BaseUseCase<Order, SubmitOrderFailure>() {

    private val callback = object : Callback<OrderResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(SubmitOrderFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: OrderResponse) {
            val result = Either.Right(Order(response.orderEntity.orderId, response.mealEntity.mealId,
                    response.orderEntity.patronId, response.orderEntity.patronName, response.orderEntity.orders, response.orderEntity.acceptedStatus))
            postToMainThread(result)
        }
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Retrieving meals for patron: $mealId")
        getRepository().submitOrder(OrderRequest(mealId, numberOrders, patronId, patronName), callback)
    }
}