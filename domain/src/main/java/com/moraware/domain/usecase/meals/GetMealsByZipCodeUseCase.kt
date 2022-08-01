package com.moraware.domain.usecase.meals

import com.moraware.domain.usecase.base.BaseUseCase

class GetMealsByZipCodeUseCase(nearbyZipCodes: List<Int>) : BaseUseCase<MealsByZipCode, GetMealsByZipCodeFailure>() {
    override suspend fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}