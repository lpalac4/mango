package com.moraware.mango.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.SettingsFragmentBinding
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.main.MainActivity

class SettingsFragment : ViewModelFragment<SettingsViewModel>() {

    lateinit var binding: SettingsFragmentBinding
    var userArg: String = ""

    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName
        fun newInstance(userId: String) = SettingsFragment().apply {
            this.userArg = userId
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        binding.model = mModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java).apply { userId.value = userArg}
        mModel.initNotificationPreference(mSharedPrefs.notificationsEnabled.isNotEmpty())

        mModel.onLoggedOut.observe(this, Observer {
            mSharedPrefs.logout()
            activity?.let { startActivity(LoginActivity.newIntent(it)) }
        })

        mModel.onLogin.observe(this, Observer {
            activity?.let { startActivity(LoginActivity.newIntent(it)) }
        })

        mModel.checkMessages.observe(this, Observer {
            activity?.let { (it as MainActivity).navigateToMessages() }
        })

        mModel.changePasswordEvent.observe(this, Observer {
            ChangePasswordDialog().show(childFragmentManager, ChangePasswordDialog.TAG)
        })

        mModel.showPrivacyPolicyEvent.observe(this, Observer {
            activity?.let { (it as MainActivity).navigateToPrivacyPolicy() }
        })

        mModel.showTermsOfServiceEvent.observe(this, Observer {
            activity?.let { (it as MainActivity).navigateToTermsOfService() }
        })

        mModel.showAboutUsEvent.observe(this, Observer {
            AboutUsDialog().show(childFragmentManager, AboutUsDialog.TAG)
        })

        mModel.updateNotificationPreference.observe(this, Observer {
            mSharedPrefs.notificationsEnabled = it
        })
    }

}
