package com.moraware.domain.usecase.zipcodes

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Meal

class NearbyMeals(val nearbyMeals: List<Meal>): DomainResponse() {

}