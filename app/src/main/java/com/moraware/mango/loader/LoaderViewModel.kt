package com.moraware.mango.loader

import com.moraware.domain.models.User
import com.moraware.domain.usecase.login.CredentialsLoginUseCase
import com.moraware.domain.usecase.login.Login
import com.moraware.domain.usecase.login.LoginFailure
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

/**
 * Created by Luis Palacios on 7/23/18.
 */
@Suppress("UNUSED_PARAMETER")
class LoaderViewModel : BaseViewModel() {

    val showLandingPage: SingleLiveEvent<User?> = SingleLiveEvent()

    override fun loadData() {
        val user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        mLogger.log("Starting Mango, user is ${user?.username ?: "Anonymous"}")

        user?.let { mangoUser ->
            val usecase = CredentialsLoginUseCase(mangoUser.email, thirdPartyId = mangoUser.uid)
            mUseCaseClient.execute({ it.either(::onUserLoginFailure, ::onUserLoginSuccess) }, usecase)
        } ?: showLandingPage.apply { value = null }.call()
    }

    private fun onUserLoginSuccess(user: Login) {
        showLandingPage.value = user.user
    }

    private fun onUserLoginFailure(failure: LoginFailure) {
        showLandingPage.value = null
    }

    private fun errorStartingApp() {
        setErrorMessage(R.string.error_message_starting_app)
    }
}