package com.moraware.mango.dependencyinjection.controller

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class ControllerModule(private val mActivity: AppCompatActivity) {

    @Provides
    fun providesContext(): Context {
        return mActivity.applicationContext
    }
}