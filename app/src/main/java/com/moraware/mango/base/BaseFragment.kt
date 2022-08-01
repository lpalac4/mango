package com.moraware.mango.base

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.dependencyinjection.controller.ControllerModule
import com.moraware.mango.dependencyinjection.controller.IControllerComponent
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.util.MangoSharedPrefs
import javax.inject.Inject

/**
 * Created by Luis Palacios on 7/27/17.
 */

abstract class BaseFragment : Fragment(), MangoAlertDialog.IAlertCallback {

    companion object {
        const val SIGN_IN_REQUEST = 1
    }

    @Inject
    lateinit var mLogger: MangoLogger

    @Inject
    lateinit var mSharedPrefs: MangoSharedPrefs

    @UiThread
    fun getControllerComponent(): IControllerComponent {
        return (activity?.application as MangoApplication)
                .getApplicationComponent()
                .newControllerComponent(ControllerModule(activity as AppCompatActivity))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getControllerComponent().inject(this)
    }

    override fun onDialogNegative(requestCode: Int) {
        if(SIGN_IN_REQUEST == requestCode) {

        }
    }

    override fun onDialogPositive(requestCode: Int) {
        if(SIGN_IN_REQUEST == requestCode) {
            context?.let { startActivity(LoginActivity.newIntent(it)) }
        }
    }

    fun showSignInModal() {
        MangoAlertDialog.newInstanceWithoutTitle(SIGN_IN_REQUEST, getString(R.string.sign_in_modal_title),
                getString(R.string.sig_in_modal_message),
                R.string.sign_in_okay,
                R.string.sign_in_cancel).show(childFragmentManager, MangoAlertDialog.TAG)
    }


}
