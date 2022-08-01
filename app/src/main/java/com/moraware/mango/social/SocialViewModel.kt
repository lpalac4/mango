package com.moraware.mango.social

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Meal
import com.moraware.domain.models.UserThumbnail
import com.moraware.domain.usecase.meals.GetMeals
import com.moraware.domain.usecase.meals.GetMealsFailure
import com.moraware.domain.usecase.meals.GetMealsUseCase
import com.moraware.domain.usecase.profile.*
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

class SocialViewModel: BaseViewModel() {

    var currentUser: RoomMangoUser? = null
    var profileImageUrl = MutableLiveData<String>().apply { value = "" }
    var username = MutableLiveData<String>().apply { value = "" }
    var userId = ""
    var email = MutableLiveData<String>().apply { value = "" }
    var profileBio = MutableLiveData<String>().apply { value = "" }
    var showProfile = MutableLiveData<Boolean>().apply { value = false }
    var isCurrentUser = MutableLiveData<Boolean>().apply { value = false }
    var followStatus = MutableLiveData<Int>().apply { value = R.string.social_follow }
    var showFollowButton = MutableLiveData<Boolean>().apply { value = false }

    var profiles = MutableLiveData<MutableList<UserThumbnail>>()
    var meals = MutableLiveData<List<Meal>>()
    var displayingMeals = MutableLiveData<Boolean>().apply { false }
    var displayingProfiles = MutableLiveData<Boolean>().apply { false }
    var additionalDataProcessing = MutableLiveData<Boolean>().apply { value = false }
    var followingDataProcessing = MutableLiveData<Boolean>().apply { value = false }
    var emptyFollowersMeals = MutableLiveData<Boolean>().apply { value = false }

    var viewMealEvent = SingleLiveEvent<Meal>()
    var viewProfileEvent = SingleLiveEvent<UserThumbnail>()
    var viewSettingsEvent = SingleLiveEvent<Unit>()
    var viewMessagesEvent = SingleLiveEvent<Unit>()
    var changePhotoEvent = SingleLiveEvent<String>()
    var showSignInEvent = SingleLiveEvent<Unit>()
    var changeBioEvent = SingleLiveEvent<Unit>()

    override fun loadData() {
        if (username.value?.isNotEmpty() == true) return

        currentUser = runBlocking {
            MangoApplication.getUsersBlocking()
        }

        if (currentUser == null) {
            showSignInEvent.call()
            return
        }

        setProcessing(true)

        showFollowButton.value = currentUser != null && currentUser?.uid.equals(userId) == false
        isCurrentUser.value = currentUser?.uid.equals(userId)

        val useCase = GetProfileUseCase(currentUser?.uid ?: "", userId)
        mUseCaseClient.execute({ it.either(::onGetUserFailure, ::onGetUserSuccess) }, useCase)
    }

    private fun onGetUserSuccess(profile: Profile) {
        setProcessing(false)

        val user = profile.user
        username.value = user.username
        profileImageUrl.value = user.photoUrl
        email.value = user.email
        profileBio.value = user.bio
        followStatus.value = if (user.following == true) R.string.social_unfollow else R.string.social_follow

        showProfile.value = user.bio.isNotEmpty() || isCurrentUser.value == true

        if (isCurrentUser.value == true) loadFollowers() else loadMeals()
    }

    private fun onGetUserFailure(getProfileFailure: GetProfileFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_loading_social_user)
    }

    private fun loadFollowers() {
        displayingMeals.value = false
        displayingProfiles.value = true

        additionalDataProcessing.value = true
        emptyFollowersMeals.value = false
        val useCase = GetFollowersUseCase(userId)
        mUseCaseClient.execute({ it.either(::onGetFollowersFailure, ::onGetFollowersSuccess) }, useCase)
    }

    private fun onGetFollowersSuccess(followers: SocialNetwork) {
        additionalDataProcessing.value = false
        profiles.value = followers.network
        emptyFollowersMeals.value = followers.network.isEmpty()
    }

    private fun onGetFollowersFailure(getFollowersFailure: GetFollowersFailure) {
        additionalDataProcessing.value = false
        profiles.value = mutableListOf()
        emptyFollowersMeals.value = true
    }

    private fun loadMeals() {
        displayingMeals.value = true
        displayingProfiles.value = false

        additionalDataProcessing.value = true
        emptyFollowersMeals.value = false
        val useCase = GetMealsUseCase(userId)
        mUseCaseClient.execute({ it.either(::onGetMealsFailure, ::onGetMealsSuccess) }, useCase)
    }

    private fun onGetMealsSuccess(response: GetMeals) {
        additionalDataProcessing.value = false
        meals.value = response.meals
        emptyFollowersMeals.value = response.meals.isEmpty()
    }

    private fun onGetMealsFailure(failure: GetMealsFailure) {
        additionalDataProcessing.value = false
        meals.value = emptyList()
        emptyFollowersMeals.value = true
    }

    fun showSettings() {
        viewSettingsEvent.call()
    }

    fun showMessages() {
        viewMessagesEvent.call()
    }

    fun followUserToggle() {
        val following = followStatus.value == R.string.social_unfollow

        followingDataProcessing.value = true
        val useCase = UpdateFollowersUseCase(following, currentUser?.uid
                ?: "", currentUser?.photoUrl ?: "",
                currentUser?.username ?: "", userId, profileImageUrl.value ?: "", username.value
                ?: "")
        mUseCaseClient.execute({ it.either(::onFollowFailure, ::onFollowSuccess) }, useCase)
    }

    private fun onFollowSuccess(updatedFollowers: UpdatedFollowers) {
        followingDataProcessing.value = false
        followStatus.value = if (updatedFollowers.deleted) R.string.social_follow else R.string.social_unfollow
        if (updatedFollowers.deleted) profiles.value?.remove(updatedFollowers.updateFollower) else profiles.value?.add(updatedFollowers.updateFollower)
    }

    private fun onFollowFailure(failure: UpdateFollowersFailure) {
        followingDataProcessing.value = false
        setErrorMessage(R.string.error_following)
    }

    fun onChangePhoto() {
        if (isCurrentUser.value == true) changePhotoEvent.value = userId
    }

    fun openProfile(profile: UserThumbnail) {
        viewProfileEvent.value = profile
    }

    fun openMeal(meal: Meal) {
        viewMealEvent.value = meal
    }

    fun onChefMealsSelected() {
        loadMeals()
    }

    fun onPatronsSelected() {
        loadFollowers()
    }

    fun onChangeBio() {
        changeBioEvent.call()
    }

    fun updateBio(input: String) {
        setProcessing(true)
        val useCase = UpdateProfileUseCase(currentUser?.uid ?: "", bio = input)
        mUseCaseClient.execute({ it.either(::onUpdateBioFailure, ::onUpdateBio) }, useCase)
    }

    private fun onUpdateBio(profile: Profile) {
        setProcessing(false)
        profileBio.value = profile.user.bio
    }

    private fun onUpdateBioFailure(updateProfileFailure: UpdateProfileFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_updating_bio)
    }
}