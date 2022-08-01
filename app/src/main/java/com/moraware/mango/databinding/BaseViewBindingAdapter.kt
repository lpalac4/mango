package com.moraware.mango.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData

class BaseViewBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("android:visibility")
        fun setVisibility(view: View, show: Boolean?) {
            if (show == null) {
                view.visibility = View.GONE
                return
            }

            view.visibility = if (show) View.VISIBLE else View.GONE
        }

        @JvmStatic
        @BindingAdapter("android:visibility")
        fun setVisibility(view: View, show: Boolean) {
            view.visibility = if (show) View.VISIBLE else View.GONE
        }

        @JvmStatic
        @BindingAdapter("android:visibility")
        fun <T> setVisibility(view: View, data: MutableLiveData<T>) {
            view.visibility = if(data.value == null) View.GONE else View.VISIBLE
        }

        @JvmStatic
        @BindingAdapter("inverseVisibility")
        fun <T> inverseVisibility(view: View, data: MutableLiveData<T>) {
            view.visibility = if(data.value == null) View.VISIBLE else View.GONE
        }
    }
}