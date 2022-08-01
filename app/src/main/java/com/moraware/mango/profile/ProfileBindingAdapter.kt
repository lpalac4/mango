package com.moraware.mango.profile

import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter

class ProfileBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("onNotificationCheckListener")
        fun setNotificationListener(switch: SwitchCompat, viewModel: SettingsViewModel) {
            switch.setOnCheckedChangeListener { _, checked ->

            }
        }
    }
}