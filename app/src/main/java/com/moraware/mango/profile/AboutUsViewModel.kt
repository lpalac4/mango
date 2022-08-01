package com.moraware.mango.profile

import com.moraware.mango.BuildConfig
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent

class AboutUsViewModel: BaseViewModel() {

    var openTwitterEvent = SingleLiveEvent<Unit>()
    var openGithubEvent = SingleLiveEvent<Unit>()
    var version: String = BuildConfig.VERSION_NAME

    override fun loadData() {

    }

    fun onOpenGithub() {
        openGithubEvent.call()
    }

    fun onOpenTwitter() {
        openTwitterEvent.call()
    }
}