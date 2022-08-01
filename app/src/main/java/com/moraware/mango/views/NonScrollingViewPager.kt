package com.moraware.mango.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonScrollingViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var disable = true

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !disable && super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return !disable && super.onTouchEvent(event)
    }
}