package com.moraware.mango.login

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityLoginBinding
import com.moraware.mango.main.MainActivity
import com.moraware.mango.webview.WebViewFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : ViewModelActivity<LoginViewModel>() {

    private lateinit var googleAuthenticator: GoogleAuthenticator
    private lateinit var binding: ActivityLoginBinding

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginModel = mViewModel

        binding.password.setOnEditorActionListener(mPasswordChangeListener)

        mViewModel.mValidationState.observe(this, mViewModel.getValidationStateObserver())
        mViewModel.showMainScreenComponent.observe(this, Observer {
            showMainScreenComponent(it)
        })
        mViewModel.focusEmail.observe(this, Observer {
            focusEmail()
        })
        mViewModel.focusPassword.observe(this, Observer {
            focusPassword()
        })
        mViewModel.showNewUserModal.observe(this, Observer {
            showNewUserModal(it)
        })
        mViewModel.showThirdPartyRegistrationModal.observe(this, Observer {
            showNewUserModal(it)
        })

        return binding
    }

    private val mPasswordChangeListener = TextView.OnEditorActionListener { _, id, _ ->
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            mViewModel.attemptToLogin()
            return@OnEditorActionListener true
        }

        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleAuthenticator.RC_SIGN_IN) {
            mViewModel.handleSignInFromGoogle(data)
        }

        if (requestCode == CreateUserActivity.PICK_IMAGE) {
            supportFragmentManager.findFragmentByTag(CREATEUSER_FRAGMENT_TAG)?.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onGoogleSignIn() {
        googleAuthenticator = GoogleAuthenticator(this, mViewModel)
        googleAuthenticator.signInWithGoogle()
    }

    fun showMainScreenComponent(email: String?) {
        email?.let { mSharedPrefs.saveCredentials(it) }
        val displayToS = !mSharedPrefs.termsOfServiceAccepted

        if(displayToS) navigateToTermsOfService() else startActivity(MainActivity.newIntent(this))
    }

    fun focusEmail() {
        binding.email.requestFocus()
    }

    fun focusPassword() {
        binding.password.requestFocus()
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(binding.email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }

        return false
    }

    private fun showNewUserModal(name: String) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        createUserIntent.putExtra(USERNAME_ARG, name)
        startActivity(createUserIntent)
    }

    private fun showNewUserModal(data: LoginViewModel.ThirdPartyData?) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        createUserIntent.putExtra(THIRD_PARTY_USER_DATA, data)
        startActivity(createUserIntent)
    }

    private fun navigateToTermsOfService() {
        var fragment = supportFragmentManager.findFragmentByTag(WebViewFragment.TAG)
        if(fragment == null) fragment = WebViewFragment.newInstanceForAsset(getString(R.string.terms_of_service), true)
        else (fragment as WebViewFragment).onLoadNewBundle(true, getString(R.string.terms_of_service), true)

        fragment.addConfirmCallback {
            mSharedPrefs.termsOfServiceAccepted = true
            startActivity(MainActivity.newIntent(this))
        }

        pushFragmentOntoStack(fragment, WebViewFragment.TAG)
    }

    private fun pushFragmentOntoStack(fragment: Fragment, tag: String) {
        val fTrans = supportFragmentManager.beginTransaction()

        loader_tos_container.visibility = View.VISIBLE
        fTrans.replace(binding.loaderTosContainer.id, fragment, tag)
        fTrans.commit()
    }

    companion object {

        const val TAG = "LoginActivity"
        const val USERNAME_ARG = "NewUserName"
        const val CREATEUSER_FRAGMENT_TAG = "CreateUserFragment"
        const val THIRD_PARTY_USER_DATA = "ThirdPartyData"
        private const val REQUEST_READ_CONTACTS = 0

        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        .or(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
    }
}

