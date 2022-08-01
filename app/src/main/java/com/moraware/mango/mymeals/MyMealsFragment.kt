package com.moraware.mango.mymeals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.FragmentMyMealsBinding
import com.moraware.mango.login.LoginActivity
import com.moraware.mango.product.ProductActivity
import com.moraware.mango.views.StartPaddingItemDecorator

/**
 * Created by root on 8/1/17.
 */

class MyMealsFragment : ViewModelFragment<MyMealsViewModel>() {

    companion object {
        val TAG = MyMealsFragment::class.java.simpleName
        const val START_ON_CHEF_PAGE = "MyMealsFragment.startOnChefPage"

        fun newInstance(startOnChefPage: Boolean? = null): Fragment {
            var bundle = Bundle()
            startOnChefPage?.let { bundle.putBoolean(START_ON_CHEF_PAGE, startOnChefPage) }
            val fragment = MyMealsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentMyMealsBinding
    private var adapter: MyMealsPagerItemAdapter? = null
    var startOnOrders = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(MyMealsViewModel::class.java)
        mModel.onMealFavorited.observe(this, Observer {

        })

        mModel.onLoginRequired.observe(this, Observer {
            context?.let { it.startActivity(LoginActivity.newIntent(it)) }
        })

        mModel.navigateToMealPage.observe(this, Observer {
            val intent = Intent(this@MyMealsFragment.activity, ProductActivity::class.java)
            intent.putExtras(ProductActivity.createIntentExtras(it))
            startActivity(intent)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_meals, container, false)
        if (adapter == null) {
            adapter = MyMealsPagerItemAdapter(activity!!, mModel)
        }

        val layoutManager = GridLayoutManager(context,
                resources.getInteger(R.integer.mymeals_number_columns), RecyclerView.VERTICAL, false)
        binding.pagerMyMeals.layoutManager = layoutManager
        binding.pagerMyMeals.adapter = adapter
        val dividerItemDecoration = StartPaddingItemDecorator(resources.getDimensionPixelSize(R.dimen.mymeals_recycler_spacing), layoutManager.orientation, layoutManager.spanCount)
        binding.pagerMyMeals.addItemDecoration(dividerItemDecoration)

        if (MyMealsViewModel.State.default() != mModel.state) {
            binding.tabLayoutMyMeals.getTabAt(1)?.select()
        } else {
            arguments?.getBoolean(START_ON_CHEF_PAGE)?.let {
                if(it) {
                    binding.tabLayoutMyMeals.getTabAt(1)?.select()
                    mModel.state = MyMealsViewModel.State.MY_COOKED_MEALS
                }
            }
        }

        binding.tabLayoutMyMeals.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.let {
//                    binding.pagerMyMeals.setCurrentItem(p0.position, true)
                    when (p0.position) {
                        0 -> mModel.onOrderedMealsSelected()
                        1 -> mModel.onChefMealsSelected()
                        else -> mLogger.log("Tabs are not working correctly under My Meals.")
                    }
                }
            }

        })

        binding.viewModel = mModel
        binding.lifecycleOwner = this
        return binding.root
    }
}
