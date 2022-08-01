package com.moraware.data.entities

open class OrderEntity(
        val mealId: String = "",
        val patronId: String = "",
        val patronName: String = "",
        val orders: Int = 0,
        var acceptedStatus: Boolean = false
) {
    var orderId: String = ""
}