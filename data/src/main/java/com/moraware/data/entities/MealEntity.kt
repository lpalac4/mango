package com.moraware.data.entities

import java.util.Date

open class MealEntity(
        var mealId: String = "",

        var name: String = "",

        var zipCode: String = "",

        var shortDescription: String = "",

        var longDescription: String = "",

        var featuredImage: String = "",

        var images: List<String> = emptyList(),

        var videos: List<String> = emptyList(),

        var placeholder: String = "",

        var liked: List<String> = emptyList(),

        var chefId: String = "",

        var chefName: String = "",

        var chefPhotoUrl: String = "",

        var city: String = "",

        var patrons: MutableMap<String, Boolean> = mutableMapOf(),

        var orderId: String = "",

        var maxOrders: Int = 0,

        var orders: Int = 0,

        var eta: Date? = null,

        var ingredients: List<String> = emptyList(),

        var allergens: List<String> = emptyList(),

        var imageDescription: List<String> = emptyList(),

        var servingsPerOrder: Int = 1
) {

    var geoLocation: GeoLocation = GeoLocation.NONE
}