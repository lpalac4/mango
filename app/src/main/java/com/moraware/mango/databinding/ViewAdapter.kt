package com.moraware.mango.databinding

import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter

/**
 * Created by luis palacios on 7/26/17.
 */

class ViewAdapter {

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

//        @JvmStatic
//        @BindingAdapter("android:visibility")
//        fun setVisibility(view: Button, content: String?) {
//            view.visibility = if (content != null && content.isNotEmpty()) View.VISIBLE else View.GONE
//        }

//        @JvmStatic
//        @BindingAdapter("android:visibility")
//        fun setVisibility(view: Button, content: String) {
//            view.visibility = if (content.isNotEmpty()) View.VISIBLE else View.GONE
//        }
    }
}
