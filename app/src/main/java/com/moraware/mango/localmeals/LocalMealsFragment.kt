package com.moraware.mango.localmeals

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter
import com.moraware.mango.databinding.FragmentLocalMealsBinding
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.product.ProductActivity
import com.moraware.mango.util.Utils.Companion.distanceFrom
import com.moraware.mango.views.SimpleDividerItemDecoration

/**
 * Created by Luis Palacios on 8/1/17.
 */

const val MAP_DEFAULT_ZOOM: Float = 14f
class LocalMealsFragment : ViewModelFragment<LocalMealsViewModel>(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var binding: FragmentLocalMealsBinding
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<CardView>

    companion object {

        val TAG = LocalMealsFragment::class.java.simpleName
        const val LOCATION: String = "LocalMealsFragment_arg_location"

        fun newInstance(location: Location?): LocalMealsFragment {
            var args = Bundle()
            if(location != null) args.putParcelable(LOCATION, location)
            val fragment = LocalMealsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun setLocation(location: Location?) {
        location?.let {
            mModel.setCurrentLocation(it)
            val currentLatLng = LatLng(location.latitude, location.longitude)
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MAP_DEFAULT_ZOOM))
        }
    }

    private val mMarkerClickListener: GoogleMap.OnMarkerClickListener? = GoogleMap.OnMarkerClickListener { marker ->
        if(marker != null && marker.tag is String)
        {
            mModel.setSelectedMeal(marker.tag as String)
            true
        }
        else
            false
    }

    private val mMapMoveListener: GoogleMap.OnCameraIdleListener? = GoogleMap.OnCameraIdleListener {
        mMap?.let {
            val visibleRegion = it.projection.visibleRegion
            val nearLeft = visibleRegion.nearLeft
            val nearRight = visibleRegion.nearRight
            val farLeft = visibleRegion.farLeft
            val farRight = visibleRegion.farRight
            mModel.setCurrentBoundaries(MapBoundaries(nearLeft, nearRight, farLeft, farRight))
            val distW = distanceFrom(nearLeft.latitude, nearLeft.longitude, nearRight.latitude, nearRight.longitude)
            val distH = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude)
            Log.d("DISTANCE: ", "DISTANCE WIDTH: $distW DISTANCE HEIGHT: $distH")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val currentLatLng = LatLng(mModel.getCurrentLocation()?.latitude ?: 0.0, mModel.getCurrentLocation()?.longitude ?: 0.0)

        mMap = googleMap
        context?.let {
            mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.google_maps_style))
        }
        mMap?.setOnMarkerClickListener(mMarkerClickListener)
        mMap?.setOnCameraIdleListener(mMapMoveListener)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MAP_DEFAULT_ZOOM))
        mModel.getLocalMeals().observe(this, Observer { localMeals ->
            if (localMeals != null && !localMeals.isEmpty()) {
                for (meal in localMeals) {
                    addMealMarker(meal)
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_local_meals, container, false)
        binding.localMealsMapview.getMapAsync(this)
        binding.localMealsMapview.onCreate(savedInstanceState)

        binding.mealImageRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.mealImageRecycler.adapter = LocalMealsImageAdapter(this, mModel)
        binding.mealImageRecycler.addItemDecoration(SimpleDividerItemDecoration(context!!, SimpleDividerItemDecoration.HORIZONTAL))

        ViewCompat.postOnAnimation(binding.localMealsCoordinator) { ViewCompat.postInvalidateOnAnimation(binding.localMealsCoordinator) }
        mBottomSheetBehavior = BottomSheetBehavior.from(binding.localMealsBottomsheet)

        binding.viewModel = mModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mModel = ViewModelProviders.of(it).get(LocalMealsViewModel::class.java)
            mModel.getLocalZipCodes().observe(this, Observer { zipCodes ->
                if (zipCodes == null || zipCodes.isEmpty()) {
                    Log.d(TAG, "No zipcodes available to display on Google Map")
                } else {
                    mModel.getMealsForZipCode(zipCodes)
                }
            })

            arguments?.getParcelable<Location>(LOCATION)?.let { location ->
                mModel.setCurrentLocation(location)
            }

            mModel.selectedMealDetailsEvent.observe(this, Observer {

            })

            mModel.showMealDetailsEvent.observe(this, Observer { meal ->
                val intent = Intent(it, ProductActivity::class.java)
                intent.putExtras(ProductActivity.createIntentExtras(meal))
                startActivity(intent)
            })

            mModel.selectedMealOnMap.observe(this, Observer { meal: ItemThumbnail? ->
                binding.selectedMeal = meal

                val shouldCollapse = meal == null
                val isCollapsed = mBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED

                if (isCollapsed && !shouldCollapse)
                    binding.localMealsBottomsheet.post {
                        addressBottomSheetBug()
                        mBottomSheetBehavior.peekHeight = 0
                        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
            })
        }
    }

    override fun onStart() {
        super.onStart()

        binding.localMealsMapview.onStart()
    }

    override fun onResume() {
        super.onResume()

        binding.localMealsMapview.onResume()
    }

    override fun onPause() {
        super.onPause()

        binding.localMealsMapview.onPause()
    }

    override fun onStop() {
        super.onStop()

        binding.localMealsMapview.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        mModel.setSelectedMeal("")
        binding.localMealsMapview.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.localMealsMapview.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        binding.localMealsMapview.onLowMemory()
    }

    fun showMealInformation(meal : ItemThumbnail) {
        binding.selectedMeal = meal
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun addMealMarker(meal: ItemThumbnail) {
        var marker = mMap?.addMarker(MarkerOptions()
                .position(LatLng(meal.mLat, meal.mLng)).title(meal.mName)
                .icon(markerDrawable))
        marker?.tag = meal.mMealId
    }

    private var markerDrawable: BitmapDescriptor? = null
        get() {
            if (field == null) markerDrawable = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)
            return field
        }

    // TODO: https://stackoverflow.com/questions/36249844/android-bottomsheet-textview-height-not-adjusted-on-first-time-expanding
    var bsBugAddressed = false
    private fun addressBottomSheetBug() {
        if(bsBugAddressed) return

        bsBugAddressed = true
        binding.localMealsBottomsheet.requestLayout()
        binding.localMealsBottomsheet.invalidate()
    }

    class LocalMealsImageAdapter(context: LocalMealsFragment, model: LocalMealsViewModel) : BaseRecyclerviewDataBindingAdapter(model) {

        var selectedMeal: ItemThumbnail? = null

        init {
            model.selectedMealOnMap.observe(context, Observer {
                selectedMeal = it
                notifyDataSetChanged()
            })
        }

        override fun getObjForPosition(position: Int): Any {
            return selectedMeal?.mImageUrls?.get(position) ?: ""
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.local_meal_adapter_item
        }

        override fun getItemCount(): Int {
            return selectedMeal?.mImageUrls?.size ?: 0
        }

    }
}
