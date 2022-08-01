package com.moraware.mango.loader

import android.app.ActivityOptions
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.domain.models.User
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityLoaderBinding
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.main.MainActivity
import com.moraware.mango.webview.WebViewFragment
import kotlinx.android.synthetic.main.activity_loader.*
import kotlin.random.Random


/**
 * Created by Luis Palacios on 7/28/17
 */
class LoaderActivity : ViewModelActivity<LoaderViewModel>() {
    companion object {
        val TAG: String = LoaderActivity::class.java.simpleName
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and bottom_navigation bar.
     */
    private val UI_ANIMATION_DELAY = 1500L
    private lateinit var binding: ActivityLoaderBinding

    private val mHandler = Handler()
    private var mVisible: Boolean = false

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_loader)
        binding.loaderLogoBackground.setImageResource(getLoaderBackground())
        mVisible = true
        mViewModel = ViewModelProviders.of(this).get(LoaderViewModel::class.java)
        mViewModel.showLandingPage.observe(this, Observer {
            mHandler.postDelayed({
                showLandingPage(it)
            }, UI_ANIMATION_DELAY)
        })

        val nightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        if(nightMode != mSharedPrefs.darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(if(mSharedPrefs.darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        return binding
    }

    private fun getLoaderBackground() : Int {
        return when(Random.nextInt(0,3)){
            0 -> R.drawable.loader_background1
            1 -> R.drawable.loader_background2
            2 -> R.drawable.loader_background3
            else -> R.drawable.loader_background2
        }
    }

    private fun showLandingPage(user: User?) {
        user?.email?.let {
            mSharedPrefs.saveCredentials(it)
            val displayToS = !mSharedPrefs.termsOfServiceAccepted
            if(displayToS) navigateToTermsOfService() else navigateToLandingPage(true)
        } ?: navigateToLandingPage(user != null)
    }

    override fun onStart() {
        val blinking = AnimationUtils.loadAnimation(this, R.anim.blink)
        blinking.reset()
        blinking.repeatMode = Animation.INFINITE

        super.onStart()
    }

    private fun navigateToLandingPage(authenticated: Boolean) {
        if(authenticated) {
            startActivity(MainActivity.newIntent(this))
        } else {
            startActivity(LoginActivity.newIntent(this))
        }
    }

    private fun navigateToTermsOfService() {
        var fragment = supportFragmentManager.findFragmentByTag(WebViewFragment.TAG)
        if(fragment == null) fragment = WebViewFragment.newInstanceForAsset(getString(R.string.terms_of_service), true)
        else (fragment as WebViewFragment).onLoadNewBundle(true, getString(R.string.terms_of_service), true)

        fragment.addConfirmCallback {
            mSharedPrefs.termsOfServiceAccepted = true
            navigateToLandingPage(true)
        }

        pushFragmentOntoStack(fragment, WebViewFragment.TAG)
    }

    private fun pushFragmentOntoStack(fragment: Fragment, tag: String) {
        val fTrans = supportFragmentManager.beginTransaction()

        loader_tos_container.visibility = View.VISIBLE
        fTrans.replace(binding.loaderTosContainer.id, fragment, tag)
        fTrans.commit()
    }
}
