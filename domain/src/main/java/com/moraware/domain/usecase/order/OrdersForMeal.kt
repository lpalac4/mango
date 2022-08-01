package com.moraware.domain.usecase.order

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Order

class OrdersForMeal(val orders: MutableList<Order>) : DomainResponse() {

}