package com.moraware.mango.profile

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.usecase.logout.Logout
import com.moraware.domain.usecase.logout.LogoutFailure
import com.moraware.domain.usecase.logout.LogoutUseCase
import com.moraware.domain.usecase.profile.Profile
import com.moraware.domain.usecase.profile.UpdateProfileFailure
import com.moraware.domain.usecase.profile.UpdateProfileUseCase
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

class SettingsViewModel : BaseViewModel() {

    private var currentUser: RoomMangoUser? = null
    var userId = MutableLiveData<String>().apply { value = "" }
    var email= MutableLiveData<String>().apply { value = "" }
    var name= MutableLiveData<String>().apply { value = "" }
    var authenticated= MutableLiveData<Boolean>().apply { value = false }
    var id= MutableLiveData<String>().apply { value = "" }
    var allowNotifications= MutableLiveData<Boolean>().apply { value = false }
    var thirdPartyId= MutableLiveData<String>().apply { value = "" }
    var profileImage= MutableLiveData<String>().apply { value = "" }

    var onLoggedOut: SingleLiveEvent<Any> = SingleLiveEvent()
    var onLogin: SingleLiveEvent<Any> = SingleLiveEvent()
    var checkMessages: SingleLiveEvent<Any> = SingleLiveEvent()
    var changePasswordEvent = SingleLiveEvent<Unit>()
    var showPrivacyPolicyEvent = SingleLiveEvent<Unit>()
    var showTermsOfServiceEvent = SingleLiveEvent<Unit>()
    var showAboutUsEvent = SingleLiveEvent<Unit>()
    var updateNotificationPreference = SingleLiveEvent<String>()

    override fun loadData() {
        setProcessing(true)
        currentUser = runBlocking {
            MangoApplication.getUsersBlocking()
        }

        if (currentUser == null) {
            onLogin.call()
            return
        }

        updateModel()
        setProcessing(false)
    }

    fun updateModel() {
        mLogger.log("Updating data model.")
        this.authenticated.value = currentUser?.username?.isNotEmpty()
        this.userId.value = currentUser?.username
        this.name.value = currentUser?.name
        this.email.value = currentUser?.email
        this.id.value = currentUser?.uid
        this.thirdPartyId.value = currentUser?.thirdPartyToken
        this.profileImage.value = currentUser?.photoUrl
    }

    fun initNotificationPreference(enabled: Boolean) {
        this.allowNotifications.value = enabled
    }

    fun changeName() {
        mLogger.log("Attempting to change user's name.")
    }

    fun changeUserName() {
        mLogger.log("Attempting to change user's username.")
    }

    fun changeEmail() {
        mLogger.log("Attempting to change user's email.")
    }

    fun checkMessages() {
        mLogger.log("Attempting to checking messages.")
        checkMessages.call()
    }

    fun changePassword() {
        mLogger.log("Attempting to change user's password.")
        changePasswordEvent.call()
    }

    fun changeImage() {
        mLogger.log("Attempting to change user's image.")
    }

    fun changeNotificationPreference() {
        mLogger.log("Attempting to change user's notification password.")
        if(allowNotifications.value == false) {
            currentUser?.notificationTokens?.isNotEmpty()?.let {
                changeNotificationPreference(true, currentUser?.notificationTokens)
            }
        }
        else {
            changeNotificationPreference(false, null)
        }
    }

    fun changeNotificationPreference(enabled: Boolean, token: MutableList<String>?) {
        setProcessing(true)
        val useCase = UpdateProfileUseCase(currentUser?.uid ?: "", notificationEnabled = enabled, notificationTokensToAdd = token)
        mUseCaseClient.execute({ it.either(::onUpdateNotifSettingFailure, ::onUpdateNotifSetting) }, useCase)
    }

    private fun onUpdateNotifSetting(profile: Profile) {
        setProcessing(false)
        allowNotifications.value = profile.user.notificationToken.isNotEmpty()
        updateNotificationPreference.value = if(profile.user.notificationToken.isNotEmpty()) profile.user.email else ""
    }

    private fun onUpdateNotifSettingFailure(updateProfileFailure: UpdateProfileFailure) {
        setProcessing(false)
    }

    fun showPrivacyPolicy() {
        mLogger.log("Displaying support screen.")
        showPrivacyPolicyEvent.call()
    }

    fun showTermsOfService() {
        mLogger.log("Displaying FAQ screen.")
        showTermsOfServiceEvent.call()
    }

    fun showAboutUs() {
        mLogger.log("Displaying Chef Info screen.")
        showAboutUsEvent.call()
    }

    fun logout() {
        setProcessing(true)
        val logoutUseCase = LogoutUseCase(userId.value ?: "", id.value ?: "", thirdPartyId.value ?: "")
        mUseCaseClient.execute({ it.either(::onLogoutFailure, ::onLogout) }, logoutUseCase)
    }

    private fun onLogout(response: Logout) {
        setProcessing(false)
        onLoggedOut.call()
    }

    private fun onLogoutFailure(failure: LogoutFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_logging_out)
    }

    fun login() {
        onLogin.call()
    }
}
