package com.moraware.mango.localmeals

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.moraware.domain.usecase.meals.GetMealsByZipCodeFailure
import com.moraware.domain.usecase.meals.GetMealsByZipCodeUseCase
import com.moraware.domain.usecase.meals.MealsByZipCode
import com.moraware.domain.usecase.zipcodes.GetNearbyMealsByBoundaries
import com.moraware.domain.usecase.zipcodes.GetNearbyMealsFailure
import com.moraware.domain.usecase.zipcodes.NearbyMeals
import com.moraware.mango.MangoApplication
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

/**
 * Created by Luis Palacios on 8/16/17.
 */
data class MapBoundaries(val bottomLeft: LatLng,
                         val topLeft: LatLng,
                         val bottomRight: LatLng,
                         val topRight: LatLng)

class LocalMealsViewModel : BaseViewModel() {

    private var mUserId: RoomMangoUser? = null
    private var mCurrentLocation: Location? = null
    private var mCurrentBoundaries: MutableLiveData<MapBoundaries?> = MutableLiveData()
    private var mZipcodes: MutableLiveData<List<Int>> = MutableLiveData()
    private var mLocalMeals: MutableLiveData<List<ItemThumbnail>> = MutableLiveData()
    var selectedMealOnMap: MutableLiveData<ItemThumbnail?> = MutableLiveData()
    var selectedMealDetailsEvent = SingleLiveEvent<ItemThumbnail>()
    var showMealDetailsEvent = SingleLiveEvent<ItemThumbnail>()

    fun showMealDetails(meal: ItemThumbnail) {
        showMealDetailsEvent.value = meal
    }

    fun getLocalZipCodes(): MutableLiveData<List<Int>> {
        return mZipcodes
    }

    fun setCurrentBoundaries(boundaries: MapBoundaries) {
        mCurrentBoundaries.value = boundaries
        retrieveNearbyMealsByBoundaries(boundaries)
    }

    fun setCurrentLocation(location: Location) {
        mCurrentLocation = location
    }

    fun getCurrentLocation(): Location? {
        return mCurrentLocation
    }

    fun getCurrentBoundaries(): MutableLiveData<MapBoundaries?> {
        return mCurrentBoundaries
    }

    fun getLocalMeals(): MutableLiveData<List<ItemThumbnail>> {
        return mLocalMeals
    }

    fun setSelectedMeal(id: String) {
        if(id.isEmpty()){
            selectedMealOnMap.value = null
            return
        }

        mLocalMeals.value?.let {
            for (meal in it) {
                if (id == meal.mMealId) selectedMealOnMap.value = meal
            }
        }
    }

    fun retrieveNearbyMealsByBoundaries(boundaries: MapBoundaries) {
        var useCase = GetNearbyMealsByBoundaries(doubleArrayOf(boundaries.topRight.latitude, boundaries.topRight.longitude),
                doubleArrayOf(boundaries.bottomLeft.latitude, boundaries.bottomLeft.longitude), mUserId?.uid ?: "")

        mUseCaseClient.execute({it.either(::onGetNearbyMealsError, ::onGetNearbyMeals)}, useCase)
    }

    fun onMealsByAddress(response: MealsByZipCode) {
        mLocalMeals.value = ItemThumbnail.fromMeals(response.meals)
    }

    fun onMealsByAddressFailure(error: GetMealsByZipCodeFailure) {
        mLogger.log("Error retrieving nearby meals.")
    }

    fun onGetNearbyMeals(response: NearbyMeals) {
        mLogger.log("Successfully loaded ${response.nearbyMeals.size} nearby meals.")
        mLocalMeals.value = ItemThumbnail.fromMeals(response.nearbyMeals)
    }

    fun onGetNearbyMealsError(error: GetNearbyMealsFailure) {
        mLogger.log("Error retrieving nearby meals.")
    }

    override fun loadData() {
        mUserId = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }
    }

    fun getMealsForZipCode(zipCodes: List<Int>) {
        val useCase = GetMealsByZipCodeUseCase(zipCodes)
        mUseCaseClient.execute({it.either(::onMealsByAddressFailure, ::onMealsByAddress)}, useCase)
    }

    fun showSelectedMealDetails() {
        selectedMealDetailsEvent.value = selectedMealOnMap.value
    }
}
