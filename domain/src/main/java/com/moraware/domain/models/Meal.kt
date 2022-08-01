package com.moraware.domain.models

import com.moraware.domain.interactors.DomainResponse
import java.util.Date

/**
 * Created by Luis Palacios on 7/29/17.
 */

class Meal : DomainResponse() {

    var mealId: String = ""

    var name: String = ""

    var zipCode: String = ""

    var description: String = ""

    var featuredImage: String = ""

    var imageUrls: List<String> = emptyList()

    var favorite: Boolean = false

    var ordered: Boolean = false

    var videosAvailable: Boolean = false

    var videoUrls: List<String> = emptyList()

    var placeholder: String = ""

    var chefId: String = ""

    var chefName: String = ""

    var chefPhotoUrl: String = ""

    var patrons: MutableMap<String, Boolean> = mutableMapOf()

    var orderId: String = ""

    var maxOrders: Int = 0

    var eta: Date? = null

    var ingredients: List<String> = emptyList()

    var allergens: List<String> = emptyList()

    var numberOfFavorites: Int = 0

    var servingsPerOrder: Int = 0

    var numberOfOrders: Int = 0

    var latitude: Double? = null

    var longitude: Double? = null

    var imageDescriptions: List<String> = emptyList()

    var city: String = ""

    var chefAwarded: Boolean = false
}
