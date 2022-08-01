package com.moraware.mango.login

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.moraware.domain.usecase.login.AnonymousLoginUseCase
import com.moraware.domain.usecase.login.CredentialsLoginUseCase
import com.moraware.domain.usecase.login.Login
import com.moraware.domain.usecase.login.LoginFailure
import com.moraware.domain.usecase.user.GetUserFailure
import com.moraware.domain.usecase.user.GetUserUseCase
import com.moraware.domain.usecase.user.GetUser
import com.moraware.mango.BR
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent
import java.io.Serializable

/**
 * Created by luis palacios on 7/25/17.
 */

class LoginViewModel : BaseViewModel(), IAuthenticatorView {
    override fun onAuthenticatorInitError() {
        showMainScreenComponent.value = null
    }

    override fun onAuthenticationSuccessfullyLinked() {
        setHasError(true)
        setErrorMessage(R.string.error_login)
    }

    override fun loadData() {
        val useCase = GetUserUseCase()
        mUseCaseClient.execute({ it.either(::onAuthFailure, ::onAuthSuccess)}, useCase)
    }

    val showMainScreenComponent = SingleLiveEvent<String?>()
    val focusPassword = SingleLiveEvent<Any>()
    val focusEmail = SingleLiveEvent<Any>()
    val showNewUserModal = SingleLiveEvent<String>()
    val showThirdPartyRegistrationModal = SingleLiveEvent<ThirdPartyData>()

    /** start of member variables **/
    var mValidationState: MutableLiveData<Int> = MutableLiveData()

    private val isEmailValid: Boolean
        get() = (name.isNotEmpty()
                && name.contains("@"))

    private val isPasswordValid: Boolean
        get() = (password.isNotEmpty()
                && password.length > 4)

    var name: String = ""
        @Bindable
        get() = field
        set(name) {
            if (this.name != name) {
                field = name
                notifyPropertyChanged(BR.name)
            }
        }

    var password: String = ""
        @Bindable
        get() = field
        set(password) {
            if (this.password != password) {
                field = password
                notifyPropertyChanged(BR.password)
            }
        }

    var errorText: String = ""
        @Bindable
        get() = field
        set(message) {
            if (errorText != message) {
                field = message
                notifyPropertyChanged(BR.errorText)
            }
        }

    /** end of member variables **/

    private fun onAuthSuccess(response: GetUser) {
        response.user.email.isNotEmpty().let {
            createAuthenticatedLoginModel(response.user.email)
        }
    }
    private fun onAuthFailure(response: GetUserFailure) {
        createUnauthenticatedLoginModel()
    }

    private fun createAuthenticatedLoginModel(user: String) {
        name = user
        password = ""
    }

    private fun createUnauthenticatedLoginModel() {
        name = ""
        password = ""
    }

    fun attemptToLogin() {
        mValidationState.value = VALIDATE_STATE_DEFAULT

        // Store values at the time of the login attempt.
        if (!isEmailValid) {
            mValidationState.value = VALIDATE_STATE_EMAIL_VALIDATION_ERROR
            return
        } else if (!isPasswordValid) {
            mValidationState.value = VALIDATE_STATE_PASSWORD_VALIDATION_ERROR
            return
        }

        setProcessing(true)
        mValidationState.value = VALIDATE_STATE_VALIDATED
    }

    fun getValidationStateObserver(): Observer<Int> {
        return Observer { state ->
            when (state) {
                null -> {
                    Log.e(LoginActivity.TAG, "Validation state error.")
                }
                VALIDATE_STATE_DEFAULT -> {
                    errorText = ""
                }
                VALIDATE_STATE_PASSWORD_VALIDATION_ERROR -> {
                    errorText = MangoApplication.getInstance().getString(R.string.error_invalid_password)
                    focusPassword.call()
                    return@Observer
                }

                VALIDATE_STATE_EMAIL_VALIDATION_ERROR -> {
                    errorText = MangoApplication.getInstance().getString(R.string.error_invalid_email)
                    focusEmail.call()
                    return@Observer
                }

                VALIDATE_STATE_VALIDATED -> {
                    signInWithEmailAndPassword(name, password)
                }
            }
        }
    }

    fun onAnonymousSignIn() {
        setProcessing(true)

        val useCase = AnonymousLoginUseCase()

        mLogger.log("Attempting to login Anonymously.")
        mUseCaseClient.execute({it.either(::onLoginFailure, ::onLoginSuccess)}, useCase)
    }

    fun onCreateNewUser() {
        showNewUserModal.value = ""
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        mLogger.log("Attempting to login with email and password.")
        val useCase = CredentialsLoginUseCase(email, password)

        mUseCaseClient.execute({it.either(::onLoginFailure, ::onLoginSuccess)}, useCase)
    }

    /** start of observers and callbacks **/

    /**
     * TODO: decide if this is something we're okay supporting...
     */
    fun handleSignInFromGoogle(data: Intent?) {
        data?.let {
            //TODO: login with google, check if thirdPartyId appears in user table - if not create new account
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d(GoogleAuthenticator.TAG, "handleSignInResult:" + result.isSuccess)

            if (result.isSuccess && result.signInAccount != null) {
                val acct = result.signInAccount

                acct?.let {
                    if(it.email != null)
                        showThirdPartyRegistrationModal.value = ThirdPartyData(it)
                }
            } else {
                onThirdPartyLoginError("Google")
            }
        }
    }

    fun onThirdPartyLoginError(serviceName: String) {
        setProcessing(false)
        setHasError(true)
        setErrorMessage(serviceName, R.string.third_party_error_login)
    }

    fun onLoginSuccess(response: Login) {
        setProcessing(false)

        response.user.let {
            mLogger.log("Log in success, showing home page.")
            showMainScreenComponent.value = it.email
        }
    }

    fun onLoginFailure(error: LoginFailure) {
        mLogger.log("Login failure.")
        setProcessing(false)
        setHasError(true)
        setErrorMessage(R.string.error_login)
    }

    /** end of observers and callbacks **/

    class ThirdPartyData : Serializable {
        var firstName: String = ""
        var lastName: String = ""
        var username: String = ""
        var email: String = ""
        var id: String = ""
        var idToken: String = ""
        var photo: Uri?

        constructor(account: GoogleSignInAccount) {
            firstName = account.givenName ?: ""
            lastName = account.familyName ?: ""
            username = account.displayName ?: ""
            email = account.email ?: ""
            id = account.id ?: ""
            idToken = account.idToken ?: ""
            photo = account.photoUrl
        }

    }

    companion object {
        const val VALIDATE_STATE_DEFAULT = 0
        const val VALIDATE_STATE_EMAIL_VALIDATION_ERROR = 1
        const val VALIDATE_STATE_PASSWORD_VALIDATION_ERROR = 2
        const val VALIDATE_STATE_VALIDATED = 3

        private val isGoogleEnabled: Boolean
            get() = false

        private val isFacebookEnabled: Boolean
            get() = false
    }
}
