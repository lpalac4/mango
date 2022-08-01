package com.moraware.mango.createmeal

import android.location.Address
import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Meal
import com.moraware.domain.usecase.meals.SubmitMealFailure
import com.moraware.domain.usecase.meals.SubmitMealUseCase
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking
import org.threeten.bp.ZonedDateTime
import java.net.URI
import java.util.*

class CreateMealViewModel: BaseViewModel() {

    companion object {
        const val PHOTO = 0
        const val NAME = 1
        const val DESCRIPTION = 2
        const val RECIPE = 3
        const val ALLERGENS = 4
        const val ORDERS = 5
        const val LOCATION = 6
    }

    var state = 0

    var userId = MutableLiveData<String>().apply { value = "" }
    var userName = MutableLiveData<String>().apply { value = "" }
    var userPhotoUrl = MutableLiveData<String>().apply { value = "" }
    var imageUris = MutableLiveData<MutableList<URI>>().apply { value = mutableListOf() }
    var mealName = MutableLiveData<String>().apply { value = "" }
    var mealDescription = MutableLiveData<String>().apply { value = "" }
    var ingredientMap = mutableMapOf<Int, String>()

    var containsSoy = MutableLiveData<Boolean>().apply { value = false }
    var containsDairy = MutableLiveData<Boolean>().apply { value = false }
    var containsNuts = MutableLiveData<Boolean>().apply { value = false }
    var containsShellfish = MutableLiveData<Boolean>().apply { value = false }
    var containsWheat = MutableLiveData<Boolean>().apply { value = false }
    var containsEggs = MutableLiveData<Boolean>().apply { value = false }

    var mealNotice = MutableLiveData<String>().apply { value = "" }
    var zipCode = MutableLiveData<String>().apply { value = "" }
    var city = MutableLiveData<String>().apply { value = "" }
    var latitude = MutableLiveData<Double>().apply { value = 0.0 }
    var longitude = MutableLiveData<Double>().apply { value = 0.0 }


    var date = MutableLiveData<ZonedDateTime>().apply { value = ZonedDateTime.now().plusMinutes(10) }

    var addressString = MutableLiveData<String>().apply { value = "" }

    var mealCreated = SingleLiveEvent<Meal>()
    var showDateTimePicker = SingleLiveEvent<Unit>()
    var onAddPhotoEvent = SingleLiveEvent<CreateMealPhotoAdapter.PhotoItem>()
    var onRemovePhotoEvent = SingleLiveEvent<CreateMealPhotoAdapter.PhotoItem>()
    var onLastPage = MutableLiveData<Boolean>().apply { value = false }
    var onContinueEvent = SingleLiveEvent<Unit>()

    private fun canSubmit(): Boolean {
        return (ingredientMap.isNotEmpty()
                && mealDescription.value?.isNotEmpty() == true && mealDescription.value!!.length > 5
                && mealName.value?.isNotEmpty() == true && mealName.value!!.length > 2
                && imageUris.value?.isNotEmpty() ?: false)
    }

    fun onSubmit() {
        if(onLastPage.value == false) {
            onContinueEvent.call()
            return
        }

        if(!canSubmit()) {
            setErrorMessage(R.string.insufficient_meal_information)
            return
        }

        setProcessing(true)
        val useCase = SubmitMealUseCase(userId.value ?: "", userName.value ?: "", userPhotoUrl.value ?: "",
                mealName.value ?: "", mealDescription.value ?: "",
                imageUris.value ?: mutableListOf(), ingredientMap.values.toList(),
                containsSoy.value ?: false, containsDairy.value ?: false,
                containsNuts.value ?: false, containsShellfish.value ?: false,
                containsWheat.value ?: false, containsEggs.value ?: false,
                mealNotice.value ?: "", latitude.value ?: 0.0, longitude.value ?: 0.0,
                city.value ?: "", Date(date.value?.toInstant()?.toEpochMilli() ?: 0), zipCode.value ?: "")
        mUseCaseClient.execute({ it.either(::onSubmitMealFailure, ::onSubmitMealSuccess) }, useCase)

    }

    fun onSubmitMealSuccess(meal: Meal) {
        setProcessing(false)
        mealCreated.value = meal
    }

    fun onSubmitMealFailure(failure: SubmitMealFailure) {
        setProcessing(false)
        setErrorMessage(R.string.create_meal_error)
    }

    override fun loadData() {
        val user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        userId.value = user?.uid ?: ""
        userName.value = user?.username ?: ""
        userPhotoUrl.value = user?.photoUrl ?: ""
    }

    fun onRemoveIngredient(id: Int) {
        ingredientMap.remove(id)
    }

    fun getNewIngredientId(): Int {
        return ingredientMap.size + 1
    }

    fun onContainsEggs() {
        containsEggs.value = containsEggs.value?.not() ?: false
    }

    fun onContainsWheat() {
        containsWheat.value = containsWheat.value?.not() ?: false
    }

    fun onContainsShellfish() {
        containsShellfish.value = containsShellfish.value?.not() ?: false
    }

    fun onContainsNuts() {
        containsNuts.value = containsNuts.value?.not() ?: false
    }

    fun onContainsDairy() {
        containsDairy.value = containsDairy.value?.not() ?: false
    }

    fun onContainsSoy() {
        containsSoy.value = containsSoy.value?.not() ?: false
    }

    fun setAddress(address: Address) {
        zipCode.value = address.postalCode
        city.value = address.locality
        latitude.value = address.latitude
        longitude.value = address.longitude

        addressString.value = address.getAddressLine(0)
    }

    fun onChooseDate() {
        showDateTimePicker.call()
    }

    fun setChosenDate(chosenDate: ZonedDateTime) {
        date.value = chosenDate
    }

    fun onAddPhoto(index: CreateMealPhotoAdapter.PhotoItem) {
        onAddPhotoEvent.value = index
    }

    fun onRemovePhoto(item: CreateMealPhotoAdapter.PhotoItem) {
        imageUris.value?.remove(item.javaURI)
        onRemovePhotoEvent.value = item
    }

    fun addPhotoUri(javaUri: URI) {
        imageUris.value?.add(javaUri)
    }

    fun swap(javaUri1: URI?, javaUri2: URI?) {
        javaUri1?.let { uri1 ->
            javaUri2?.let { uri2 ->
                val index1 = imageUris.value?.indexOf(uri1) ?: 0
                val index2 = imageUris.value?.indexOf(uri2) ?: 0

                imageUris.value?.let { Collections.swap(it, index1, index2) }
            }
        }
    }

    fun setOnLastPage(value: Boolean) {
        onLastPage.value = value
    }
}