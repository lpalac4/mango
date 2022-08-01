package com.moraware.data.models

import com.moraware.data.base.BaseRequest
import java.net.URI
import java.util.*

class SubmitMealRequest(val userId: String, val userName: String, val userPhotoUrl: String, val mealName: String, val mealDescription: String,
                        val imageUris: List<URI>, val ingredientMap: List<String>, val allergenMap: List<String>,
                        val mealNotice: String, val latitude: Double, val longitude: Double, val city: String,
                        val eta: Date, val zipCode: String) : BaseRequest() {
}