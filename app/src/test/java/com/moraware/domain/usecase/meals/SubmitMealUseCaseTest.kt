package com.moraware.domain.usecase.meals

import com.moraware.domain.client.DomainDependencyProvider
import com.moraware.mango.dependencyinjection.MockLogger
import com.moraware.mango.dependencyinjection.MockRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class SubmitMealUseCaseTest {
    @Before
    fun setUp() {
        DomainDependencyProvider.setDataRepository(MockRepository())
        DomainDependencyProvider.setLogger(MockLogger())
    }

    @Test
    fun run() {
        val soyUseCase = SubmitMealUseCase(
            userId = "testUserId",
            userName = "testUserName",
            userPhotoUrl = "testUserPhotoUrl",
            mealName = "testMealName",
            mealDescription = "testMealDescription",
            imageUris = emptyList(),
            ingredientMap = emptyList(),
            containsSoy = true,
            containsDairy = false,
            containsNuts = false,
            containsShellfish = false,
            containsWheat = false,
            containsEggs = false,
            mealNotice = "testMealNotice",
            latitude = 0.0,
            longitude = 0.0,
            city = "testCity",
            eta = Date(),
            zipCode = "testZipCode"
        )

        assertEquals(soyUseCase.hasAllergens(), true)
    }
}