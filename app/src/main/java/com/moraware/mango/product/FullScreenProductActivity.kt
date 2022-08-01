package com.moraware.mango.product

import android.os.Bundle
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityFullScreenProductBinding
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.order.OrderOptionsDialogFragment

class FullScreenProductActivity : ViewModelActivity<ProductViewModel>(), OrderOptionsDialogFragment.OrderListener {
    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen_product)

        mViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        if (intent.extras?.get(FULL_SCREEN_EXTRAS_MEAL_KEY) != null) {
            mViewModel.setProduct(intent.extras?.get(FULL_SCREEN_EXTRAS_MEAL_KEY) as ItemThumbnail)
        }

        mViewModel.orderProductEvent.observe(this, Observer {
            OrderOptionsDialogFragment.submitOrderInstance(mViewModel).show(supportFragmentManager, OrderOptionsDialogFragment.TAG)
        })

        mViewModel.imageClickedEvent.observe(this, Observer {
            finish()
        })

        mViewModel.checkOrdersEvent.observe(this, Observer {
            OrderOptionsDialogFragment.checkOrdersInstance(mViewModel).show(supportFragmentManager, OrderOptionsDialogFragment.TAG)
        })

        binding.productModel = mViewModel
        val adapter = ProductImageAdapter(mViewModel, ImageView.ScaleType.FIT_CENTER, true)
        binding.productImagePager.adapter = adapter

        return binding
    }

    lateinit var binding: ActivityFullScreenProductBinding

    override fun orderFailure() {

    }

    override fun orderSuccess(thumbnail: ItemThumbnail) {
        mViewModel.setProduct(thumbnail)
        Snackbar.make(binding.root, "Your meal has been reserved, keep an eye on your 'Ordered Meals'", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    override fun orderUpdated(thumbnail: ItemThumbnail) {
        mViewModel.setProduct(thumbnail)
    }

    companion object {

        val FULL_SCREEN_EXTRAS_MEAL_KEY = FullScreenProductActivity::class.java.simpleName
    }
}
