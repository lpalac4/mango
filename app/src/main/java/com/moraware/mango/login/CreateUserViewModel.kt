package com.moraware.mango.login

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.moraware.domain.usecase.user.RegisterUserFailure
import com.moraware.domain.usecase.user.RegisterUserUseCase
import com.moraware.domain.usecase.user.GetUser
import com.moraware.mango.BR
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent
import com.moraware.mango.util.Utils
import java.net.URI
import java.util.regex.Pattern

/** only contains alphanumeric characters, underscore and dot with dot and underscore not next to each other. Must be between 6-20 chars **/
const val userNameRegex = "^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$"
/** one upper, one numeric, and at least 6 chars **/
const val passwordRegex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}"

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isUserNameValid(userName: String): Boolean {
    return Pattern.compile(userNameRegex).matcher(userName).matches()
}

fun isPasswordValid(password: String): Boolean {
    return Pattern.compile(passwordRegex).matcher(password).matches()
}

class CreateUserViewModel: BaseViewModel() {

    /**
    ^                 # start-of-string
    [a-zA-Z@#$%^&+=]  # first digit letter or special character
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once
    .{8,}             # anything, at least eight places though
    [a-zA-Z0-9]       # last digit letter or number
    $                 # end-of-string*/

    val profilePlaceholder = R.drawable.profile_placeholder

    // user vars
    var userName = ""
    var password = ""
    var email = ""
    var imageUri = MutableLiveData<URI>()

    var accountCreatedEvent = SingleLiveEvent<String>()
    var onSelectImageEvent = SingleLiveEvent<Any>()

    fun udpateWithThirdPartyData(data: LoginViewModel.ThirdPartyData) {
        userName = data.username
        email = data.email
        imageUri.value = Utils.uriToURI(data.photo)
    }

    override fun loadData() {
        filledOut = false
    }

    var filledOut: Boolean = false
        @Bindable
        get() = field
        set(value) {
            if(filledOut != value) {
                field = value
                notifyPropertyChanged(BR.filledOut)
            }
        }

    fun createUser() {
        var valid = isEmailValid(email) && isUserNameValid(userName) && isPasswordValid(password) && imageUri != null

        if(!valid) {
            setErrorMessage(R.string.creation_user_error)
            return
        }

        if(valid) {
            registerAccount()
        } else {
            setErrorMessage(R.string.error_account_validation)
        }
    }

    fun registerAccount() {
        mLogger.log("Attempting to register account.")
        setProcessing(true)
        val usecase = RegisterUserUseCase(userName, email, password, imageUri.value)

        mUseCaseClient.execute({it.either(::onRegisterError, ::onRegisterSuccess)}, usecase)
    }

    fun onRegisterError(failure: RegisterUserFailure) {
        mLogger.log("Failure registering account.")
        setProcessing(false)
        setErrorMessage(R.string.error_register_account)
    }

    fun onRegisterSuccess(user: GetUser) {
        mLogger.log("Success registering account: ${user.user.name}")
        setProcessing(false)
        accountCreatedEvent.value = user.user.email
    }

    fun onSelectImage() {
        onSelectImageEvent.call()
    }

    fun imageSelected(data: URI) {
        imageUri.value = data
    }
}