package com.moraware.domain.usecase.order

import com.moraware.domain.interactors.DomainResponse

class ConfirmOrder(val orderId: String, val acceptedStatus: Boolean, val numberOfOrders: Int): DomainResponse() {
}