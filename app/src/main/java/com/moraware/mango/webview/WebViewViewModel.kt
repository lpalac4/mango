package com.moraware.mango.webview

import androidx.lifecycle.MutableLiveData
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent

class WebViewViewModel: BaseViewModel() {

    var title = MutableLiveData<String>().apply { value = ""}
    var requiresAccept = MutableLiveData<Boolean>().apply { value = true }
    var url = MutableLiveData<String>().apply { value = "" }
    var assetPath = MutableLiveData<String>().apply { value = "" }

    var onActionTakeEvent = SingleLiveEvent<Unit>()

    override fun loadData() {

    }

    fun onActionTaken() {
        onActionTakeEvent.call()
    }

    fun initForAssetPath(value: String) {
       assetPath.value = value
    }

    fun initForUrl(value: String) {
        url.value = value
    }

    fun setRequiresAccept(requirement: Boolean) {
        requiresAccept.value = requirement
    }
}