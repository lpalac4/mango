package com.moraware.mango.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.R.id.places_autocomplete_search_input
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.moraware.mango.R
import com.moraware.mango.base.BaseLocationActivity
import com.moraware.mango.base.MangoAlertDialog
import com.moraware.mango.createmeal.CreateMealActivity
import com.moraware.mango.databinding.ActivityMainBinding
import com.moraware.mango.featured.FeaturedFragment
import com.moraware.mango.localmeals.LocalMealsFragment
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.messages.MessagesActivity
import com.moraware.mango.mymeals.MyMealsFragment
import com.moraware.mango.profile.SettingsFragment
import com.moraware.mango.social.SocialFragment
import com.moraware.mango.util.MangoLocationManager
import com.moraware.mango.webview.WebViewFragment
import java.util.logging.Level


class MainActivity : BaseLocationActivity<MainActivityViewModel>(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var addressString = ""
    private var hasRequestNotifications = false

    companion object {
        private const val NOTIFICATIONS_ENABLED_FOR_EMAIL = 3211
        private const val INITIAL_START = "MainActivity.InitialStart"
        fun newIntent(context: Context, initialStart: Boolean = false): Intent {
            val bundle = Bundle()
            bundle.putBoolean(INITIAL_START, initialStart)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP
                        .or(FLAG_ACTIVITY_SINGLE_TOP)
                        .or(FLAG_ACTIVITY_NEW_TASK)
                        .or(FLAG_ACTIVITY_CLEAR_TASK)
            }

            intent.putExtras(bundle)
            return intent
        }
    }

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)

        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        binding.drawerLayout.setDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)
        toggle.syncState()

        mViewModel.signInActionEvent.observe(this, Observer {
            startActivity(LoginActivity.newIntent(this))
        })

        mViewModel.updateCurrentLocationEvent.observe(this, Observer {
            locationManager.queryLastLocation()
        })

        mViewModel.showProfileEvent.observe(this, Observer {
            binding.navigation.selectedItemId = R.id.navigation_profile
            binding.drawerLayout.closeDrawers()
        })

        mViewModel.onNotificationPrefUpdated.observe(this, Observer {
            mSharedPrefs.notificationsEnabled = it
        })

        mViewModel.checkNotificationPrefEvent.observe(this, Observer {
            requestNotificationsPermission()
        })

        mViewModel.updateDarkModePreferenceEvent.observe(this, Observer {
            val nightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            if(nightMode != it) {
                AppCompatDelegate.setDefaultNightMode(if(it) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                mViewModel.mDarkModeEnabled.value = it
                mSharedPrefs.darkModeEnabled = it
                recreate()
            }
        })

        mViewModel.mNotificationsEnabled.value = mSharedPrefs.notificationsEnabled.isNotEmpty()

        val nightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        mViewModel.mDarkModeEnabled.value = nightMode

        setupPlaceAutocomplete()

        locationManager.setListener(locationListener)

        binding.navigation.setOnNavigationItemSelectedListener(null)
        navigateToInitialFragment(mViewModel.navigationItemId)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        return binding
    }

    private fun requestNotificationsPermission() {
        if(!hasRequestNotifications && mSharedPrefs.notificationsEnabled.isEmpty()) {
            hasRequestNotifications = true
            MangoAlertDialog.newInstanceWithTitle(NOTIFICATIONS_ENABLED_FOR_EMAIL, R.string.alert_notification_permission_title,
                    R.string.alert_notification_permission_message, R.string.notification_allow, R.string.notification_deny)
                    .show(supportFragmentManager, MangoAlertDialog.TAG)
        }
    }

    override fun onDialogNegative(requestCode: Int) {
        if(NOTIFICATIONS_ENABLED_FOR_EMAIL == requestCode) {
            return
        } else super.onDialogNegative(requestCode)
    }

    override fun onDialogPositive(requestCode: Int) {
        if(NOTIFICATIONS_ENABLED_FOR_EMAIL == requestCode) {
            mViewModel.changeNotificationPreference(true)
        } else super.onDialogPositive(requestCode)
    }

    private fun setupPlaceAutocomplete() {
        if (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) == null) return

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mLogger.log(Level.FINE, "Place: " + place.address)
                binding.navView.setCheckedItem(R.id.navigation_local_meals)
                mViewModel.navigationItemId = R.id.navigation_local_meals
                onNewLocationRequested(place)
            }

            override fun onError(status: Status) {
                mLogger.log(Level.FINE, "An error occurred: $status")
            }
        })

        findViewById<EditText>(places_autocomplete_search_input).textSize = resources.getDimension(R.dimen.search_address_textsize)
    }

    private fun onNewLocationRequested(place: Place) {
        val location = Location("default").apply {
            latitude = place.latLng?.latitude ?: 0.0
            longitude = place.latLng?.longitude ?: 0.0
        }

        binding.navigation.selectedItemId = R.id.navigation_local_meals
        binding.navView.setCheckedItem(R.id.navigation_local_meals)

        var fragment = supportFragmentManager.findFragmentByTag(LocalMealsFragment.TAG)
        if(fragment == null) fragment = LocalMealsFragment.newInstance(location) else (fragment as LocalMealsFragment).setLocation(location)

        switchMainFragment(fragment, LocalMealsFragment.TAG, R.string.title_order_nearby)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        menu?.findItem(R.id.action_create_meal)?.isVisible = mViewModel.mChef

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_meal -> {
                startActivityForResult(Intent(this, CreateMealActivity::class.java), CreateMealActivity.CREATE_MEAL_RESULT)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val locationListener = object: MangoLocationManager.ILocationListener {
        override fun onNewLocation(location: Location, addressString: String) {
            this@MainActivity.addressString = addressString
            val placesFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
            if (placesFragment == null) return else (placesFragment as AutocompleteSupportFragment).setHint(getString(R.string.search_meals_by_location))
        }
    }

    private fun allowUserToEnterLocation() {
        val placesFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
        if (placesFragment == null) return else supportFragmentManager.beginTransaction().show(placesFragment).commit()
    }

    private fun hidePlaceAutocomplete() {
        val placesFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
        if (placesFragment == null) return else supportFragmentManager.beginTransaction().hide(placesFragment).commit()
    }

    private fun resetAutoComplete() {
        val placesFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
        if (placesFragment == null) return else (placesFragment as AutocompleteSupportFragment).setHint(getString(R.string.search_meals_by_location))
    }

    /**
     * Bottom nav override
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        binding.navView.setCheckedItem(item.itemId)
        mViewModel.navigationItemId = item.itemId

        when (item.itemId) {
            R.id.navigation_featured -> {
                navigateToFeaturedFragment()
            }
            R.id.navigation_local_meals -> {
                navigateToNearbyOrders()
            }
            R.id.navigation_my_meals -> {
                navigateToMyOrders()
            }
            R.id.navigation_profile -> {
                navigateToProfile()
            }
        }

        true
    }

    /**
     * Side drawer override
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle bottom_navigation view item clicks here.
        val id = item.itemId
        val navigationView = binding.navigation

        if(id != R.id.navigation_messages) {
            navigationView.setOnNavigationItemSelectedListener(null)
            navigationView.selectedItemId = id
            navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

        mViewModel.navigationItemId = id

        when (id) {
            R.id.navigation_featured -> navigateToFeaturedFragment()
            R.id.navigation_local_meals -> navigateToNearbyOrders()
            R.id.navigation_my_meals -> navigateToMyOrders()
            R.id.navigation_messages -> navigateToMessages()
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToInitialFragment(id: Int?) {
        if (id == null) {
            binding.navigation.selectedItemId = R.id.navigation_featured
            binding.navView.setCheckedItem(R.id.navigation_featured)
            navigateToFeaturedFragment()
            return
        } else {
            binding.navigation.selectedItemId = id
            binding.navView.setCheckedItem(id)
        }

        when (id) {
            R.id.navigation_featured -> navigateToFeaturedFragment()
            R.id.navigation_local_meals -> navigateToNearbyOrders()
            R.id.navigation_my_meals -> navigateToMyOrders()
            R.id.navigation_profile -> navigateToProfile()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if(supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun navigateToFeaturedFragment() {
        val fragment: Fragment = supportFragmentManager.findFragmentByTag(FeaturedFragment.TAG)
                ?: FeaturedFragment.newInstance()

        switchMainFragment(fragment, FeaturedFragment.TAG, R.string.title_featured)
    }

    private fun navigateToProfile() {
        val fragment: Fragment = supportFragmentManager.findFragmentByTag(SocialFragment.TAG)
                ?: SocialFragment.newInstance(mViewModel.mId.value ?: "")

        switchMainFragment(fragment, SocialFragment.TAG, R.string.title_profile)
    }

    fun navigateToSettings() {
        val fragment: Fragment = supportFragmentManager.findFragmentByTag(SettingsFragment.TAG)
                ?: SettingsFragment.newInstance(mViewModel.mId.value ?: "")

        pushFragmentOntoStack(fragment, SettingsFragment.TAG)
    }

    private fun navigateToMyOrders() {
        val fragment: Fragment = supportFragmentManager.findFragmentByTag(MyMealsFragment.TAG)
                ?: MyMealsFragment.newInstance()

        switchMainFragment(fragment, MyMealsFragment.TAG, R.string.title_my_orders)
    }

    private fun navigateToNearbyOrders() {
        val location = locationManager.getLastLocation()

        var fragment = supportFragmentManager.findFragmentByTag(LocalMealsFragment.TAG)
        if(fragment == null) fragment = LocalMealsFragment.newInstance(location) else (fragment as LocalMealsFragment).setLocation(location)

        switchMainFragment(fragment, LocalMealsFragment.TAG, R.string.title_order_nearby)
    }

    fun navigateToMessages() {
        startActivity(Intent(this, MessagesActivity::class.java))
    }

    fun navigateToTermsOfService() {
        var fragment = supportFragmentManager.findFragmentByTag(WebViewFragment.TAG)
        if(fragment == null) fragment = WebViewFragment.newInstanceForAsset(getString(R.string.terms_of_service), false)
        else (fragment as WebViewFragment).onLoadNewBundle(true, getString(R.string.terms_of_service), false)

        pushFragmentOntoStack(fragment, WebViewFragment.TAG)
    }

    fun navigateToPrivacyPolicy() {
        var fragment = supportFragmentManager.findFragmentByTag(WebViewFragment.TAG)
        if(fragment == null) fragment = WebViewFragment.newInstanceForAsset(getString(R.string.privacy_policy), false)
        else (fragment as WebViewFragment).onLoadNewBundle(true, getString(R.string.privacy_policy), false)

        pushFragmentOntoStack(fragment, WebViewFragment.TAG)
    }

    private fun switchMainFragment(fragment: Fragment, tag: String, actionbarTitleRes: Int) {
        if(supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        binding.toolbar.setTitle(actionbarTitleRes)

        if(fragment is FeaturedFragment || fragment is LocalMealsFragment) allowUserToEnterLocation() else hidePlaceAutocomplete()

        if(!(fragment is LocalMealsFragment)) resetAutoComplete()

        val fTrans = supportFragmentManager.beginTransaction()

        fTrans.replace(binding.mainFragmentContainer.id, fragment, tag)
        fTrans.commit()
    }

    private fun pushFragmentOntoStack(fragment: Fragment, tag: String) {

        if(fragment is FeaturedFragment || fragment is LocalMealsFragment) allowUserToEnterLocation() else hidePlaceAutocomplete()

        if(fragment !is LocalMealsFragment) resetAutoComplete()

        val fTrans = supportFragmentManager.beginTransaction()

        fTrans.replace(binding.mainFragmentContainer.id, fragment, tag)
        fTrans.addToBackStack(tag)
        fTrans.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(CreateMealActivity.CREATE_MEAL_RESULT == requestCode && resultCode == Activity.RESULT_OK) {
            Handler().post {
                binding.navigation.selectedItemId = R.id.navigation_my_meals
                binding.navView.setCheckedItem(R.id.navigation_my_meals)

                val fragment = supportFragmentManager.findFragmentByTag(MyMealsFragment.TAG) as MyMealsFragment?

                if(fragment == null) MyMealsFragment.newInstance(true) else fragment.startOnOrders = true

                fragment?.let { switchMainFragment(it, MyMealsFragment.TAG, R.string.title_my_orders) }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
