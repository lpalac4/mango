package com.moraware.domain.mappers

import com.moraware.data.models.RetrieveOrdersForMealResponse
import com.moraware.domain.models.Order
import com.moraware.domain.usecase.order.OrdersForMeal

class OrderMapper {

    fun transform(response: RetrieveOrdersForMealResponse) : OrdersForMeal {
        val orders = mutableListOf<Order>()
        for(entity in response.orders) {
            var order = Order(entity.orderId, entity.mealId, entity.patronId, entity.patronName, entity.orders, entity.acceptedStatus)
            orders.add(order)
        }

        return OrdersForMeal(orders)
    }
}