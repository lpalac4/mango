package com.moraware.mango.createmeal

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.moraware.mango.BR
import com.moraware.mango.R
import com.moraware.mango.base.BaseLocationActivity
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.ALLERGENS
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.DESCRIPTION
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.LOCATION
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.NAME
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.ORDERS
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.PHOTO
import com.moraware.mango.createmeal.CreateMealViewModel.Companion.RECIPE
import com.moraware.mango.databinding.ActivityCreateMealBinding
import com.moraware.mango.localmeals.MAP_DEFAULT_ZOOM
import com.moraware.mango.revenue.AdWrapper
import com.moraware.mango.util.CameraUtils
import com.moraware.mango.util.MangoLocationManager
import com.moraware.mango.util.Utils
import com.moraware.mango.views.MangoDatePicker
import com.moraware.mango.views.MangoTimePicker
import com.moraware.mango.views.SpacingItemDecoration
import kotlinx.coroutines.runBlocking
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class CreateMealActivity : BaseLocationActivity<CreateMealViewModel>(), PhotoSourceDialog.IPhotoSourceDialogCallback, OnMapReadyCallback {

    lateinit var binding: ActivityCreateMealBinding
    private var adapter: CreateMealAdapter? = null
    private var photoIndexRequested: CreateMealPhotoAdapter.PhotoItem? = null
    private var cameraPictureUri: Uri? = null
    private lateinit var photoAdapter: CreateMealPhotoAdapter

    private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private var currentLocation: Location? = null

    companion object {
        const val REQUEST_PHOTO_CAMERA = 1
        const val REQUEST_PHOTO_GALLERY = 2
        const val CREATE_MEAL_RESULT = 1342
        const val PHOTO_SOURCE_FRAGMENT_TAG = "CreateMealActivity.PhotoSource"
        const val NUMBER_OF_PAGES: Int = 7
    }

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_meal)

        mViewModel = ViewModelProviders.of(this).get(CreateMealViewModel::class.java)

        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        mapView = MapView(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        photoAdapter = CreateMealPhotoAdapter(mViewModel)

        if (adapter == null) {
            adapter = CreateMealAdapter(mViewModel, mapView, photoAdapter)
            binding.createMealPager.adapter = adapter
            binding.createMealPager.offscreenPageLimit = NUMBER_OF_PAGES
            binding.dotsIndicator.setViewPager(binding.createMealPager)
            binding.createMealPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    hideKeyboard()
                }

                override fun onPageSelected(position: Int) {
                    mViewModel.setOnLastPage(position == NUMBER_OF_PAGES - 1)
                }

            })
        }

        locationManager.setListener(locationListener)
        locationManager.queryLastLocation()

        mViewModel.mealCreated.observe(this, Observer {
            onMealCreated()
        })

        mViewModel.showDateTimePicker.observe(this, Observer {
            onShowDateTimePicker()
        })

        mViewModel.onAddPhotoEvent.observe(this, Observer {
            launchPhotoSource(it)
        })

        mViewModel.onRemovePhotoEvent.observe(this, Observer {
            deleteMealPhoto(it)
        })

        mViewModel.onContinueEvent.observe(this, Observer {
            binding.createMealPager.currentItem += 1
        })

        return binding
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private val locationListener = object: MangoLocationManager.ILocationListener {
        override fun onNewLocation(location: Location, addressString: String) {
            if(currentLocation == null) {
                currentLocation = location

                if(map != null) {
                    centerMapOnCurrentLocation()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PHOTO_GALLERY && resultCode == Activity.RESULT_OK) {
            data?.data?.let { androidUri ->
                updateMealPhoto(androidUri)
            }
        } else if(requestCode == REQUEST_PHOTO_CAMERA && resultCode == Activity.RESULT_OK) {
            cameraPictureUri?.let { androidUri ->
                updateMealPhoto(androidUri)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()

        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()

        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        mapView.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.setOnCameraIdleListener(mapMoveListener)
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.google_maps_style))
        centerMapOnCurrentLocation()
    }

    private val mapMoveListener: GoogleMap.OnCameraIdleListener? = GoogleMap.OnCameraIdleListener {
        map?.let {
            runBlocking {
                val centerLtLng = it.cameraPosition.target
                mViewModel.setAddress(locationManager.computeAddress(centerLtLng.latitude, centerLtLng.longitude))
                Log.d("onMapMove: ", "Latitude: ${centerLtLng.latitude} Longitude: ${centerLtLng.longitude}")
            }
        }
    }

    private fun centerMapOnCurrentLocation() {
        val currentLatLng = LatLng(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MAP_DEFAULT_ZOOM))
    }

    private fun onMealCreated() {
        setResult(Activity.RESULT_OK)
        finish()
        AdWrapper.exposeAd()
    }

    private fun launchPhotoSource(index: CreateMealPhotoAdapter.PhotoItem) {
        setImageRequested(index)
        val createUserDialog = PhotoSourceDialog().apply { callback = this@CreateMealActivity }
        createUserDialog.arguments = Bundle()
        supportFragmentManager.beginTransaction().add(createUserDialog, PHOTO_SOURCE_FRAGMENT_TAG).commit()
    }

    override fun onCameraAsSource() {
        cameraPictureUri = CameraUtils.dispatchTakePictureIntent(this, REQUEST_PHOTO_CAMERA)
    }

    override fun onGalleryAsSource() {
        val intent = Intent()
                .apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }

        startActivityForResult(Intent.createChooser(intent, "Select Photo"), REQUEST_PHOTO_GALLERY)
    }

    private fun setImageRequested(identifier: CreateMealPhotoAdapter.PhotoItem) {
        photoIndexRequested = identifier
    }

    private fun updateMealPhoto(androidUri: Uri) {
        val javaUri = Utils.uriToURI(androidUri)
        if (javaUri == null || photoIndexRequested == null) {
            mViewModel.setErrorMessage(R.string.error_pick_image)
            return
        }

        photoIndexRequested?.let { photoItem ->
            mViewModel.addPhotoUri(javaUri)
            photoAdapter.setPhotoImageView(photoItem, androidUri, javaUri)
        }
    }

    private fun deleteMealPhoto(index: CreateMealPhotoAdapter.PhotoItem) {
        index.androidUri = null
        photoAdapter.deletePhoto(index)
    }

    private fun onShowDateTimePicker() {
        val dateFragment = MangoDatePicker(object: MangoDatePicker.Listener {
            override fun onDateChosen(year: Int, month: Int, day: Int) {
                val timeFragment = MangoTimePicker(object: MangoTimePicker.Listener {
                    override fun onTimeChose(hourOfDay: Int, minute: Int) {
                        val time = ZonedDateTime.of(year, month + 1, day, hourOfDay, minute, 0, 0, ZoneId.systemDefault())
                        mViewModel.setChosenDate(time)
                    }
                })

                timeFragment.show(supportFragmentManager, "timePicker")
            }
        })

        dateFragment.show(supportFragmentManager, "datePicker")
    }

    class CreateMealAdapter(val viewModel: CreateMealViewModel, private val mapView: MapView, private val photoAdapter: CreateMealPhotoAdapter) : PagerAdapter() {
        private lateinit var binding: ViewDataBinding
        var state: Int = 0

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return 7
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val inflater = LayoutInflater.from(container.context)
            state = position
            val layout: Int = when (position) {
                PHOTO -> {
                    R.layout.card_create_meal_photo
                }
                NAME -> {
                    R.layout.card_create_meal_name
                }
                DESCRIPTION -> {
                    R.layout.card_create_meal_description
                }
                RECIPE -> {
                    R.layout.card_create_meal_recipe
                }
                ALLERGENS -> {
                    R.layout.card_create_meal_allergens
                }
                ORDERS -> {
                    R.layout.card_create_meal_eta_picker
                }
                LOCATION -> {
                    R.layout.card_create_meal_location
                }
                else -> R.layout.card_create_meal_error
            }

            binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layout, container, false)
            binding.setVariable(BR.model, viewModel)
            binding.lifecycleOwner = container.context as LifecycleOwner

            if(LOCATION == state) {
                binding.root.findViewById<FrameLayout>(R.id.create_meal_location_map_container).addView(mapView)
            }

            if(PHOTO == state) {
                var recyclerView = binding.root.findViewById<RecyclerView>(R.id.create_meal_photo_recycler)
                recyclerView.adapter = photoAdapter

                var helper = photoAdapter.touchHelper
                helper.attachToRecyclerView(recyclerView)

                val span = container.context.resources.getInteger(R.integer.create_meal_photo_columns)
                recyclerView.layoutManager = GridLayoutManager(container.context,
                        span,
                        GridLayoutManager.VERTICAL, false)

                recyclerView.addItemDecoration(SpacingItemDecoration(span, container.context.resources.getDimensionPixelSize(R.dimen.create_meal_photo_padding), true))
            }

            binding.executePendingBindings()
            container.addView(binding.root)
            return binding.root
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }
    }
}


