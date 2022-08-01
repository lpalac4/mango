package com.moraware.mango.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.moraware.mango.R

abstract class SingleFragmentActivity : BaseActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, createFragment())
                .commit()
    }
}