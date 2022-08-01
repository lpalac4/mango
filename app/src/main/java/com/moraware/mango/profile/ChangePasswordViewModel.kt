package com.moraware.mango.profile

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.User
import com.moraware.domain.usecase.user.ChangePassword
import com.moraware.domain.usecase.user.ChangePasswordFailure
import com.moraware.domain.usecase.user.ChangePasswordUseCase
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.login.isEmailValid
import com.moraware.mango.login.isPasswordValid
import com.moraware.mango.util.SingleLiveEvent

class ChangePasswordViewModel: BaseViewModel() {

    var currentEmail = MutableLiveData<String>().apply { value = "" }
    var currentPassword = MutableLiveData<String>().apply { value = "" }
    var newPassword = MutableLiveData<String>().apply { value = "" }

    var user: User? = null

    var changePasswordSuccessEvent = SingleLiveEvent<Unit>()

    override fun loadData() {

    }

    fun changePassword() {
        val valid = isEmailValid(currentEmail.value ?: "") && isPasswordValid(currentPassword.value ?: "")
                && isPasswordValid(newPassword.value ?: "")

        if(!valid) {
            setErrorMessage(R.string.creation_user_error)
            return
        }

        val useCase = ChangePasswordUseCase(currentEmail.value ?: "", currentPassword.value ?: "", newPassword.value ?: "")
        mUseCaseClient.execute( { it.either(::onChangePasswordFailure, ::onChangePasswordSuccess)}, useCase)
    }

    private fun onChangePasswordSuccess(changePassword: ChangePassword) {
        changePasswordSuccessEvent.call()
    }

    private fun onChangePasswordFailure(changePasswordFailure: ChangePasswordFailure) {
        setErrorMessage(R.string.error_changing_password)
    }
}