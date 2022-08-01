package com.moraware.mango.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.moraware.mango.R

abstract class ViewModelFragment<T : BaseViewModel>: BaseFragment() {
    lateinit var mModel: T

    lateinit var rootView : View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
    }

    override fun onStart() {
        super.onStart()
        mModel.loadData()

        mModel.getErrorMessage().observe(this, Observer {
            Snackbar.make(rootView, getString(it), Snackbar.LENGTH_LONG).apply {
                setAction("Action", null)
                view.setBackgroundColor(resources.getColor(R.color.colorAccent))
            }.show()
        })
    }
}