package com.moraware.mango.views

import androidx.lifecycle.MutableLiveData
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent


class InputViewModel: BaseViewModel() {

    var input = MutableLiveData<String>().apply { value = "" }
    var feedback = MutableLiveData<String>().apply { value = "" }

    var validInputEvent = SingleLiveEvent<Unit>()

    override fun loadData() {

    }

    fun submitInput() {
        val valid = true

        if(!valid) {
            setHasError(true)
            return
        }

        validInputEvent.call()
    }
}