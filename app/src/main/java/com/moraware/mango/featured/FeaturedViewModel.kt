package com.moraware.mango.featured

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Meal
import com.moraware.domain.usecase.featuredmeals.FeaturedMeals
import com.moraware.domain.usecase.featuredmeals.GetFeaturedMealsFailure
import com.moraware.domain.usecase.featuredmeals.GetFeaturedMealsUseCase
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

/**
 * Created by Luis Palacios on 7/27/17.
 */

class FeaturedViewModel : BaseViewModel() {

    private var user: RoomMangoUser? = null
    var meals = mutableListOf<Meal>()
    var mFeaturedMeals = MutableLiveData<MutableList<Meal>>()
    val navigateToMealPage = SingleLiveEvent<ItemThumbnail>()
    val onMealFavorited = SingleLiveEvent<ItemThumbnail>()

    private var zipCode: String = ""

    override fun loadData() {
        setProcessing(true)
        mLogger.log("Getting featured meals.")
        user = runBlocking {
            MangoApplication.getUsersBlocking()
        }

        if(meals.isEmpty()) {
            val usecase = GetFeaturedMealsUseCase(zipCode, user?.uid ?: "")
            mUseCaseClient.execute({ it.either(::getFeaturedMealsFailure, ::getFeaturedMealsSuccess) }, usecase)
        } else setProcessing(false)
    }

    fun getFeaturedMealsSuccess(response: FeaturedMeals) {
        mLogger.log("Retrieved meals successfully, ${response.meals.size}")
        meals.addAll(response.meals)
        mFeaturedMeals.value = meals
        setProcessing(false)
    }

    fun getFeaturedMealsFailure(response: GetFeaturedMealsFailure) {
        mLogger.log("Error retrieving featured meals.")
        setErrorMessage(R.string.error_retrieving_meals)
        setProcessing(false)
    }

    fun onMealSelected(meal: ItemThumbnail) {
        navigateToMealPage.value = meal
    }

    fun onMealFavorited(meal: ItemThumbnail) {
        onMealFavorited.value = meal
    }

    override fun loadNextPage(id: Int) {
        val usecase = GetFeaturedMealsUseCase(zipCode, user?.uid ?: "")
        mUseCaseClient.execute({ it.either(::getFeaturedMealsFailure, ::getFeaturedMealsSuccess) }, usecase)
    }
}
