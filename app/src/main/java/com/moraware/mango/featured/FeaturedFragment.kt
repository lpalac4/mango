package com.moraware.mango.featured

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.FragmentFeaturedBinding
import com.moraware.mango.product.ProductActivity
import com.moraware.mango.views.StartPaddingItemDecorator


/**
 * Created by Luis Palacios on 7/28/17.
 */

class FeaturedFragment : ViewModelFragment<FeaturedViewModel>() {

    private lateinit var binding: FragmentFeaturedBinding
    private var adapter: FeaturedAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_featured, container, false)
        val layoutManager = GridLayoutManager(activity,
                resources.getInteger(R.integer.featured_number_columns), RecyclerView.VERTICAL, false)
        binding.featuredRecycler.layoutManager = layoutManager

        val dividerItemDecoration = StartPaddingItemDecorator(resources.getDimensionPixelSize(R.dimen.featured_recycler_spacing), layoutManager.orientation, layoutManager.spanCount)
        binding.featuredRecycler.addItemDecoration(dividerItemDecoration)

        binding.viewModel = mModel
        binding.lifecycleOwner = this

        if(mModel.meals.isNotEmpty()) {
            binding.featuredRecycler.adapter = adapter
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(FeaturedViewModel::class.java)

        mModel.mFeaturedMeals.observe(this, Observer { meals ->
            if(meals != null) {
                if(adapter == null) {
                    adapter = FeaturedAdapter(mModel)
                    binding.featuredRecycler.adapter = adapter

                } else {
                    adapter?.updateModels(meals)
                }
            }
        })

        mModel.onMealFavorited.observe(this, Observer {

        })

        mModel.navigateToMealPage.observe(this, Observer { it ->
            it?.let { navigateToMealPage(it)}
        })
    }

    private fun navigateToMealPage(item: ItemThumbnail) {
        val intent = Intent(this@FeaturedFragment.activity, ProductActivity::class.java)
        intent.putExtras(ProductActivity.createIntentExtras(item))
        startActivity(intent)
    }

    companion object {

        fun newInstance(): androidx.fragment.app.Fragment {
            return FeaturedFragment()
        }

        val TAG = FeaturedFragment::class.java.simpleName

    }
}
