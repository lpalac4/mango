package com.moraware.mango.dependencyinjection.application

import com.moraware.mango.security.KeyStoreManager
import com.moraware.mango.MangoApplication
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.dependencyinjection.controller.IControllerComponent
import com.moraware.mango.dependencyinjection.controller.ControllerModule
import com.moraware.mango.util.MangoSharedPrefs
import dagger.Component

/**
 * This DI component will be in charged of dependency injection at a global level, together with @ApplicationDomainScope,
 * this is effectively telling Dagger to use a single instance of these objects.
 *
 * Add more modules within this same package that will benefit from sharing injected objects. For example
 * the Application object injected through this component's ApplicationModule can be used by other modules like SettingsModule.
 */
@ApplicationScope
@Component(
        modules = [
            ApplicationModule::class,
            DomainDependencyModule::class
        ]
)
interface IApplicationComponent {

    /** Objects gettings their dependencies injected from this global level**/
    fun inject(application: MangoApplication)
    fun inject(viewModel: BaseViewModel)

    fun inject(sharedPrefs: MangoSharedPrefs)
    fun inject(keystoreManager: KeyStoreManager)

    /** components that will inject to different Android Frameworks objects **/
    fun newControllerComponent(controllerModule: ControllerModule) : IControllerComponent

}