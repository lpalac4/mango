package com.moraware.mango.main

open class MainActivityViewModelTest {

//    var viewModel: MainActivityViewModel? = null
//
//    @Rule @JvmField
//    var rule: TestRule = InstantTaskExecutorRule()
//
//    private lateinit var component: TestComponent
//
//    @Before
//    fun setUp() {
//        component = DaggerTestComponent.builder().build()
//        component.inject(this)
//    }
//
//    @Test
//    fun searchRides() {
//        viewModel = givenCurrentLocationAndDestinationIsKnown()
//        Assert.assertTrue(viewModel?.searchRides()!!)
//
//        viewModel = givenCurrentLocationIsNotKnownAndDestinationIsKnown()
//        Assert.assertFalse(viewModel?.searchRides()!!)
//
//        viewModel = givenCurrentLocationIsNotKnownAndDestinationIsNotKnown()
//        Assert.assertFalse(viewModel?.searchRides()!!)
//
//        viewModel = givenCurrentLocationKnownAndDestinationIsNotKnown()
//        Assert.assertFalse(viewModel?.searchRides()!!)
//    }
//
//    private fun givenCurrentLocationAndDestinationIsKnown() : MainActivityViewModel? {
//        viewModel = MainActivityViewModel()
//        component.inject(viewModel as BaseViewModel)
//        viewModel?.setCurrentBoundaries(Location("GPS"))
//        return viewModel
//    }
//
//    private fun givenCurrentLocationKnownAndDestinationIsNotKnown() : MainActivityViewModel? {
//        viewModel = MainActivityViewModel()
//        component.inject(viewModel as BaseViewModel)
//        viewModel?.setCurrentBoundaries(Location("GPS"))
//        return viewModel
//    }
//
//    private fun givenCurrentLocationIsNotKnownAndDestinationIsKnown() : MainActivityViewModel? {
//        viewModel = MainActivityViewModel()
//        component.inject(viewModel as BaseViewModel)
//        return viewModel
//    }
//
//    private fun givenCurrentLocationIsNotKnownAndDestinationIsNotKnown() : MainActivityViewModel? {
//        viewModel = MainActivityViewModel()
//        component.inject(viewModel as BaseViewModel)
//        return viewModel
//    }
}