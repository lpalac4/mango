package com.moraware.domain.usecase.meals

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Meal

class MealsByZipCode(val meals: ArrayList<Meal>) : DomainResponse() {
}