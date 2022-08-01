package com.moraware.mango.mymeals

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Meal
import com.moraware.domain.usecase.mymeals.GetMyMealsFailure
import com.moraware.domain.usecase.mymeals.GetMyMealsUseCase
import com.moraware.domain.usecase.mymeals.MyMeals
import com.moraware.mango.BR
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

/**
 * Created by luispalacios on 12/22/17.
 */
class MyMealsViewModel : BaseViewModel() {

    enum class State {
        MY_ORDERED_MEALS,
        MY_COOKED_MEALS;

        companion object {
            fun default(): State {
                return MY_ORDERED_MEALS
            }
        }
    }

    private var user: RoomMangoUser? = null
    val meals: MutableLiveData<List<Meal>> = MutableLiveData()
    val navigateToMealPage = SingleLiveEvent<ItemThumbnail>()
    val onMealFavorited = SingleLiveEvent<ItemThumbnail>()
    val onLoginRequired = SingleLiveEvent<Any>()

    var noResults: Boolean = false
        @Bindable
        get() = field
        set(value) {
            if (this.noResults != value) {
                field = value
                notifyPropertyChanged(BR.noResults)
            }
        }


    var state: State = State.default()

    var onOrdersTab: Boolean = State.default() == State.MY_ORDERED_MEALS
        @Bindable
        get() = field
        set(value) {
            if (this.onOrdersTab != value) {
                field = value
                notifyPropertyChanged(BR.onOrdersTab)
            }
        }

    var onChefTab: Boolean = State.default() == State.MY_COOKED_MEALS
        @Bindable
        get() = field
        set(value) {
            if (this.onChefTab != value) {
                field = value
                notifyPropertyChanged(BR.onChefTab)
            }
        }

    override fun loadData() {
        setProcessing(true)

        meals.value = emptyList()
        noResults = false
        onChefTab = state == State.MY_COOKED_MEALS
        onOrdersTab = state == State.MY_ORDERED_MEALS

        mLogger.log("Retrieving meals information: $state")
        user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        if (user == null) {
            mLogger.log("No user logged in.")
            setProcessing(false)
            onLoginRequired.call()
            return
        }

        user?.let { mangoUser ->
            when (state) {
                State.MY_ORDERED_MEALS -> {
                    val usecase = GetMyMealsUseCase(patronId = mangoUser.uid)
                    mUseCaseClient.execute({ it.either(::getMyMealsFailure, ::getMyMealsSuccess) }, usecase)
                }

                State.MY_COOKED_MEALS -> {
                    val usecase = GetMyMealsUseCase(chefId = mangoUser.uid)
                    mUseCaseClient.execute({ it.either(::getMyMealsFailure, ::getMyMealsSuccess) }, usecase)
                }
            }
        }
    }

    private fun getMyMealsSuccess(response: MyMeals) {
        mLogger.log("Retrieve a total of ${response.meals.size} meals.")
        meals.value = response.meals
        noResults = response.meals.isEmpty()
        setProcessing(false)
    }

    private fun getMyMealsFailure(response: GetMyMealsFailure) {
        mLogger.log("Failure retrieving my meals.")
        setErrorMessage(R.string.error_retrieving_meals)
        setProcessing(false)
    }

    fun onMealSelected(meal: ItemThumbnail) {
        navigateToMealPage.value = meal
    }

    fun onMealFavorited(meal: ItemThumbnail) {
        onMealFavorited.value = meal
    }

    fun onOrderedMealsSelected() {
        updateState(State.MY_ORDERED_MEALS)
    }

    fun onChefMealsSelected() {
        updateState(State.MY_COOKED_MEALS)
    }

    private fun updateState(newState: State) {
        setTabState(newState)
        loadData()
    }

    private fun setTabState(value: State) {
        if(state != value) {
            state = value
            onOrdersTab = state == State.MY_ORDERED_MEALS
            onChefTab = state == State.MY_COOKED_MEALS
        }
    }

    fun getPatronStatus(patronsMap: MutableMap<String, Boolean>) : Int {
        user?.let {
            return when (patronsMap[it.uid]) {
                true -> R.string.order_item_confirmed
                false -> R.string.order_item_requested
                else -> R.string.order_item_unknown
            }
        }

        return R.string.order_item_unknown
    }
}