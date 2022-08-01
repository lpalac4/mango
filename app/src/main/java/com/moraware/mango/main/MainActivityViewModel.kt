package com.moraware.mango.main

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.moraware.domain.usecase.profile.Profile
import com.moraware.domain.usecase.profile.UpdateProfileFailure
import com.moraware.domain.usecase.profile.UpdateProfileUseCase
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking
import java.util.logging.Level

class MainActivityViewModel : BaseViewModel() {

    private var user: RoomMangoUser? = null
    var mLocation: Location? = null
    var navigationItemId: Int? = null
    var mAuthenticated: Boolean = false
    var mId = MutableLiveData<String>().apply { value = "" }
    var mUsername = MutableLiveData<String>().apply { value = "" }
    var mEmail = MutableLiveData<String>().apply { value = "" }
    var mProfileImageUrl = MutableLiveData<String>().apply { value = "" }
    var mChef: Boolean = false
    var mNotificationsEnabled = MutableLiveData<Boolean>().apply { value = false }
    var mDarkModeEnabled = MutableLiveData<Boolean>().apply { value = false }

    val signInActionEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    val showProfileEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    val onNotificationPrefUpdated = SingleLiveEvent<String>()

    val checkNotificationPrefEvent = SingleLiveEvent<Unit>()
    val updateDarkModePreferenceEvent = SingleLiveEvent<Boolean>()

    /**
     * Initiate a scan for the current geoLocation using device services. Once devices services
     * have give us a geoLocation, store it for later use
     */

    // this single live event will trigger the hardware check
    val updateCurrentLocationEvent: SingleLiveEvent<Unit> = SingleLiveEvent()

    override fun loadData() {
        mLogger.log(Level.FINE, "Loading main activity view model data....")

        user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        if(user != null) {
            mAuthenticated = true
            mUsername.value = user?.username
            mEmail.value = user?.email
            mProfileImageUrl.value = user?.photoUrl
            mChef = user?.isChef ?: false
            mId.value = user?.uid
            checkNotificationPrefEvent.call()
        } else {
            mAuthenticated = false
        }
    }

    fun onShowProfile() {
        showProfileEvent.call()
    }

    fun changeNotificationPreference() {
        mNotificationsEnabled.value?.let { changeNotificationPreference(!it) } ?: setErrorMessage(R.string.error_changing_notification_pref)
    }

    fun changeDarkModePreference() {
        mDarkModeEnabled.value?.let { updateDarkModePreferenceEvent.value = !it }
    }

    fun changeNotificationPreference(allowNotifications: Boolean) {
        mLogger.log("Attempting to change user's notification password.")

        changeNotificationPreference(allowNotifications, if(allowNotifications) user?.notificationTokens else null)
    }

    fun changeNotificationPreference(enabled: Boolean, token: MutableList<String>?) {
        setProcessing(true)
        val useCase = UpdateProfileUseCase(user?.uid ?: "", notificationEnabled = enabled, notificationTokensToAdd = token)
        mUseCaseClient.execute({ it.either(::onUpdateNotifSettingFailure, ::onUpdateNotifSetting) }, useCase)
    }

    private fun onUpdateNotifSetting(profile: Profile) {
        setProcessing(false)
        val tokenAdded = profile.user.notificationToken.isNotEmpty()
        onNotificationPrefUpdated.value = if(tokenAdded) profile.user.email else ""
        mNotificationsEnabled.value = tokenAdded
    }

    private fun onUpdateNotifSettingFailure(updateProfileFailure: UpdateProfileFailure) {
        setProcessing(false)
        setErrorMessage(R.string.alert_notification_permission_error)
    }
}