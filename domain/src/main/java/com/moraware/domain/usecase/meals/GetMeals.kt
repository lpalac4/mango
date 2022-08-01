package com.moraware.domain.usecase.meals

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Meal

class GetMeals(val meals: List<Meal>): DomainResponse() {
}