package com.moraware.domain.usecase.meals

import com.moraware.data.base.WebServiceException
import com.moraware.data.interactors.Callback
import com.moraware.data.models.SubmitMealRequest
import com.moraware.data.models.SubmitMealResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.mappers.MealMapper
import com.moraware.domain.models.Meal
import com.moraware.domain.usecase.base.BaseUseCase
import java.net.URI
import java.util.*
import java.util.logging.Level

class SubmitMealUseCase(val userId: String, val userName: String, val userPhotoUrl: String, val mealName: String, val mealDescription: String, val imageUris: List<URI>,
                        val ingredientMap: List<String>, val containsSoy: Boolean,
                        val containsDairy: Boolean, val containsNuts: Boolean, val containsShellfish: Boolean,
                        val containsWheat: Boolean, val containsEggs: Boolean, val mealNotice: String,
                        val latitude: Double, val longitude: Double, val city: String, val eta: Date, val zipCode: String) : BaseUseCase<Meal, SubmitMealFailure>() {

    private var allergenMap = mutableListOf<String>()

    init {
        if(containsSoy) allergenMap.add("Soy")
        if(containsDairy) allergenMap.add("Dairy")
        if(containsNuts) allergenMap.add("Nut")
        if(containsShellfish) allergenMap.add("Shellfish")
        if(containsWheat) allergenMap.add("Wheat")
        if(containsEggs) allergenMap.add("Eggs")
    }

    private val callback = object : Callback<SubmitMealResponse> {
        override fun onFailure(exception: WebServiceException) {
            val result = Either.Left(SubmitMealFailure())
            postToMainThread(result)
        }

        override fun onSuccess(response: SubmitMealResponse) {
            val result = Either.Right(MealMapper().transform(response.meal, userId))
            postToMainThread(result)
        }
    }

    public fun hasAllergens() : Boolean {
        return allergenMap.size > 0
    }

    override suspend fun run() {
        mLogger?.log(Level.FINE, "Submitting meal $mealName for: $userId")
        val request = SubmitMealRequest(userId, userName, userPhotoUrl, mealName, mealDescription, imageUris, ingredientMap,
                allergenMap, mealNotice, latitude, longitude, city, eta, zipCode)

        getRepository().submitMeal(request, callback)
    }
}