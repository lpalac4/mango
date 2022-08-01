package com.moraware.mango.product

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityProductBinding
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.messages.MessagesActivity
import com.moraware.mango.order.OrderOptionsDialogFragment
import com.moraware.mango.social.SocialActivity

/**
 * Created by Luis Palacios on 8/3/17.
 */
class ProductActivity : ViewModelActivity<ProductViewModel>(), OrderOptionsDialogFragment.OrderListener {

    private lateinit var binding: ActivityProductBinding
    lateinit var snackBar: Snackbar

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?): ViewDataBinding {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        mViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        intent.extras?.let { bundle ->
            bundle.get(EXTRAS_MEAL)?.let { mViewModel.setProduct(it as ItemThumbnail) }
            bundle.getString(EXTRAS_MEAL_IDENTIFIER)?.let { mViewModel.setProductIdentifier(it) }
        }

        mViewModel.orderProductEvent.observe(this, Observer {
            OrderOptionsDialogFragment.submitOrderInstance(mViewModel).show(supportFragmentManager, OrderOptionsDialogFragment.TAG)
        })

        mViewModel.imageClickedEvent.observe(this, Observer {
            toggleFullScreenViewPager()
        })

        mViewModel.messageChefEvent.observe(this, Observer {
            showMessagesFragment(false)
        })

        mViewModel.checkOrdersEvent.observe(this, Observer {
            OrderOptionsDialogFragment.checkOrdersInstance(mViewModel).show(supportFragmentManager, OrderOptionsDialogFragment.TAG)
        })

        mViewModel.closeMealEvent.observe(this, Observer {
            Toast.makeText(this, R.string.product_meal_closed_success, Toast.LENGTH_LONG).show()
            finish()
        })

        mViewModel.productUpdated.observe(this, Observer {
            binding.productImagePager.adapter?.notifyDataSetChanged()
        })

        mViewModel.goToChefMessagesEvent.observe(this, Observer {
            showMessagesFragment(true)
        })

        mViewModel.loadChefProfileEvent.observe(this, Observer {
            showSocialActivity(it)
        })

        binding.productModel = mViewModel
        binding.lifecycleOwner = this
        binding.productImagePager.adapter = ProductImageAdapter(mViewModel, ImageView.ScaleType.CENTER_CROP, false)
        binding.productImagePager.offscreenPageLimit = 3
        binding.productImagePager.currentItem = 0

        snackBar = Snackbar.make(binding.root, getString(R.string.order_requested_needs_chef_approval), Snackbar.LENGTH_LONG).apply {
            setAction("Action", null)
            view.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }

        return binding
    }


    private fun toggleFullScreenViewPager() {
        val bundle = Bundle()
        bundle.putParcelable(FullScreenProductActivity.FULL_SCREEN_EXTRAS_MEAL_KEY, mViewModel.getProduct())
        val intent = Intent(this, FullScreenProductActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun showSocialActivity(userId: String) {
        val bundle = Bundle()
        bundle.putString(SocialActivity.USER_ID_ARG, userId)
        val intent = Intent(this, SocialActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun orderFailure() {

    }

    override fun orderSuccess(thumbnail: ItemThumbnail) {
        mViewModel.setProduct(thumbnail)
        snackBar.show()
    }

    override fun orderUpdated(thumbnail: ItemThumbnail) {
        mViewModel.setProduct(thumbnail)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun showMessagesFragment(isChef: Boolean) {
        val intent = Intent(this, MessagesActivity::class.java)
        if (!isChef) {
            intent.putExtras(MessagesActivity.newInstanceFromMeal(mViewModel.getIdentifier(), mViewModel.getMealName(),
                    mViewModel.getMealImage(), mViewModel.getChefId(), mViewModel.getChefName()))
        }

        startActivity(intent)
    }

    companion object {

        val EXTRAS_MEAL = ProductActivity::class.java.simpleName + "_extras_meal"
        val EXTRAS_MEAL_IDENTIFIER = ProductActivity::class.java.simpleName + "_extras_identifier"

        val TAG = ProductActivity::class.java.simpleName

        fun createIntentExtras(meal: ItemThumbnail): Bundle {
            val extras = Bundle()
            extras.putParcelable(EXTRAS_MEAL, meal)
            return extras
        }

        fun createIntentExtras(identifier: String): Bundle {
            val extras = Bundle()
            extras.putString(EXTRAS_MEAL_IDENTIFIER, identifier)
            return extras
        }
    }
}
