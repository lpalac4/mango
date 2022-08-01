package com.moraware.mango.dependencyinjection.controller

import com.moraware.mango.base.BaseActivity
import com.moraware.mango.base.BaseFragment
import com.moraware.mango.dependencyinjection.application.ApplicationModule
import com.moraware.mango.dependencyinjection.application.ApplicationScope
import com.moraware.mango.util.MangoSharedPrefs
import dagger.Subcomponent

/** Marking this component as a Subcomponent will grant it access to parent component **/
@Subcomponent(
        modules = [
            ControllerModule::class
        ]
)
interface IControllerComponent {

    /** Objects gettings their dependencies injected **/
    fun inject(activity: BaseActivity)
    fun inject(fragment: BaseFragment)
}