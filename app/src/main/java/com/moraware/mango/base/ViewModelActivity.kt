package com.moraware.mango.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.moraware.mango.R

abstract class ViewModelActivity<T: BaseViewModel>: BaseActivity() {

    private lateinit var rootView: View

    protected lateinit var mViewModel: T
    var mLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = setupUIAndBindViewModel(savedInstanceState).root
    }

    abstract fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding

    override fun onStart() {
        super.onStart()
        mViewModel.loadData()

        mViewModel.getErrorMessage().observe(this, Observer {
            Snackbar.make(rootView, getString(it), Snackbar.LENGTH_LONG).apply {
                setAction("Action", null)
                view.setBackgroundColor(resources.getColor(R.color.colorAccent))
            }.show()
        })
    }

    protected fun hideKeyboard() {
        val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { inputManager.hideSoftInputFromWindow(it.windowToken, 0) }
    }
}