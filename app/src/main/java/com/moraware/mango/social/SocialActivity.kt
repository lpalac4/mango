package com.moraware.mango.social

import androidx.fragment.app.Fragment
import com.moraware.mango.base.SingleFragmentActivity

class SocialActivity: SingleFragmentActivity() {

    companion object {
        val USER_ID_ARG = "SocialActivity.user_id"
    }

    override fun createFragment(): Fragment {
        return SocialFragment.newInstance(intent?.getStringExtra(USER_ID_ARG) ?: "")
    }
}