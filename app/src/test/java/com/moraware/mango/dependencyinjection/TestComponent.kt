package com.moraware.mango.dependencyinjection

import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.dependencyinjection.application.ApplicationScope
import com.moraware.mango.main.MainActivityViewModelTest
import dagger.Component

@ApplicationScope
@Component(
        modules = [
            TestModule::class,
            TestSettingsModule::class,
            TestDomainDependencyModule::class
        ]
)
interface TestComponent {
    fun inject(test: MainActivityViewModelTest)
    fun inject(viewModel: BaseViewModel)
}