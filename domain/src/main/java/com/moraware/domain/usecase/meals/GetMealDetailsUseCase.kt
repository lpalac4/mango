package com.moraware.domain.usecase.meals

import com.moraware.domain.models.Meal
import com.moraware.domain.usecase.base.BaseUseCase

class GetMealDetailsUseCase(identifier: String) : BaseUseCase<Meal, GetMealDetailsFailure>() {
    override suspend fun run() {

    }
}