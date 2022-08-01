package com.moraware.domain.usecase.featuredmeals

import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.models.Meal

class FeaturedMeals(val meals: List<Meal>) : DomainResponse() {


}
