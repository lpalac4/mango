package com.moraware.domain.usecase.order

import com.moraware.domain.interactors.DomainResponse

class CancelOrder(val orderId: String, val patronId: String, val numberOfOrders: Int): DomainResponse() {
}