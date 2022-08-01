package com.moraware.mango.base

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.moraware.mango.MangoApplication
import com.moraware.mango.dependencyinjection.controller.ControllerModule
import com.moraware.mango.dependencyinjection.controller.IControllerComponent
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.util.MangoSharedPrefs
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity(), MangoAlertDialog.IAlertCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getControllerComponent().inject(this)
    }

    @Inject
    lateinit var mLogger: MangoLogger
    @Inject
    lateinit var mSharedPrefs: MangoSharedPrefs

    @UiThread
    fun getControllerComponent(): IControllerComponent {
        return (application as MangoApplication)
                .getApplicationComponent()
                .newControllerComponent(ControllerModule(this))
    }

    fun showAlertDialog(message: String) {
        var context = ContextThemeWrapper(this, theme)
        var builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.create().show()
    }

    override fun onDialogNegative(requestCode: Int) {

    }

    override fun onDialogPositive(requestCode: Int) {
        if(BaseFragment.SIGN_IN_REQUEST == requestCode) {
            startActivity(LoginActivity.newIntent(this))
        }
    }
}