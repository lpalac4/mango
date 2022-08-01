package com.moraware.mango.databinding

import androidx.databinding.BindingAdapter
import com.google.android.material.navigation.NavigationView
import android.view.LayoutInflater
import com.moraware.mango.main.MainActivityViewModel

/**
 * Created by luis palacios on 8/2/17.
 */

object NavigationViewDataBinding {

    @JvmStatic
    @BindingAdapter("mainModel")
    fun loadHeader(view: NavigationView, model: MainActivityViewModel) {
        val binding = NavHeaderDraweractivityBinding.inflate(LayoutInflater.from(view.context))
        binding.viewModel = model
        binding.executePendingBindings()
        view.addHeaderView(binding.root)
    }
}