package com.moraware.domain.models

import com.moraware.domain.interactors.DomainResponse

class Order(val id: String, val mealId: String, val patronId: String, val patronName: String, val numberOfOrders: Int, var acceptedStatus: Boolean) : DomainResponse() {
}