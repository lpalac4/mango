package com.moraware.domain.usecase.mymeals

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Meal

class MyMeals(val meals: List<Meal>) : DomainResponse() {
}