package com.moraware.mango.social

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.moraware.domain.models.Meal
import com.moraware.domain.models.UserThumbnail
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.FragmentSocialBinding
import com.moraware.mango.main.MainActivity
import com.moraware.mango.product.ProductActivity
import com.moraware.mango.views.MangoInputDialog
import com.moraware.mango.views.SimpleDividerItemDecoration

class SocialFragment: ViewModelFragment<SocialViewModel>(), MangoInputDialog.ICallback {

    private lateinit var binding: FragmentSocialBinding
    private var profileAdapter: SocialProfilesAdapter? = null
    private var mealAdapter: SocialMealsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_social, container, false)
        binding.socialRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.socialRecycler.isNestedScrollingEnabled = false
        binding.socialRecycler.addItemDecoration(SimpleDividerItemDecoration(context!!, SimpleDividerItemDecoration.VERTICAL))
        binding.mealsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.mealsRecycler.isNestedScrollingEnabled = false
        binding.mealsRecycler.addItemDecoration(SimpleDividerItemDecoration(context!!, SimpleDividerItemDecoration.VERTICAL))
        binding.socialFollowersMealsTab.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.let {
                    when (p0.position) {
                        0 -> {
                            mModel.onPatronsSelected()
                        }
                        1->{
                            mModel.onChefMealsSelected()
                        }
                        else -> mLogger.log("Tabs are not working correctly on SocialFragment.")
                    }
                }
            }

        })

        profileAdapter = SocialProfilesAdapter(mModel)
        mealAdapter = SocialMealsAdapter(mModel)
        binding.socialRecycler.adapter = profileAdapter
        binding.mealsRecycler.adapter = mealAdapter

        val currentlySelectedTab = if(mModel.isCurrentUser.value == false) 0 else if(mModel.displayingMeals.value == true) 1 else 0
        binding.socialFollowersMealsTab.getTabAt(currentlySelectedTab)?.select()

        binding.lifecycleOwner = this
        binding.viewModel = mModel

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(SocialViewModel::class.java)
        arguments?.getString(EXTRA_USER_ID)?.let { mModel.userId = it }

        mModel.profiles.observe(this, Observer { profiles ->
            if(profiles != null) {
                profileAdapter?.updateModels(profiles)
            }
        })

        mModel.meals.observe(this, Observer { meals ->
            if(meals != null) {
                mealAdapter?.updateModels(meals)
            }
        })

        mModel.viewMealEvent.observe(this, Observer {
            navigateToMeal(it)
        })

        mModel.viewProfileEvent.observe(this, Observer {
            navigateToProfile(it)
        })

        mModel.viewSettingsEvent.observe(this, Observer {
            activity?.let { (it as MainActivity).navigateToSettings() }
        })

        mModel.viewMessagesEvent.observe(this, Observer {
            activity?.let { (it as MainActivity).navigateToMessages() }
        })

        mModel.changePhotoEvent.observe(this, Observer {

        })

        mModel.showSignInEvent.observe(this, Observer {
            showSignInModal()
        })

        mModel.changeBioEvent.observe(this, Observer {
            showInputDialog()
        })
    }

    private fun showInputDialog() {
        MangoInputDialog.newInstanceWithTitle(R.string.social_bio_title,
                R.string.social_bio_confirm,
                R.string.social_bio_feedback,
                this)
                .show(childFragmentManager, MangoInputDialog.TAG)
    }

    override fun onDialogNegative(requestCode: Int) {
        if(SIGN_IN_REQUEST == requestCode) {
            activity?.onBackPressed()
        }
    }

    private fun navigateToProfile(it: UserThumbnail) {
        val intent = Intent(this@SocialFragment.activity, SocialActivity::class.java)
        intent.putExtra(SocialActivity.USER_ID_ARG, it.id)
        startActivity(intent)
    }

    private fun navigateToMeal(meal: Meal) {
        val intent = Intent(this@SocialFragment.activity, ProductActivity::class.java)
        intent.putExtras(ProductActivity.createIntentExtras(meal.mealId))
        startActivity(intent)
    }

    override fun onInput(input: String) {
        mModel.updateBio(input)
    }

    companion object {
        fun newInstance(userId: String): Fragment {
            var bundle = Bundle()
            bundle.putString(EXTRA_USER_ID, userId)
            var fragment = SocialFragment()
            fragment.arguments = bundle
            return fragment
        }

        const val EXTRA_USER_ID = "SocialFragment_extra_user_id"
        const val TAG = "SocialFragment"

    }
}