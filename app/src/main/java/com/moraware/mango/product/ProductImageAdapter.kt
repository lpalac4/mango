package com.moraware.mango.product

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.moraware.mango.R
import com.moraware.mango.databinding.AdapterProductImageItemBinding

/**
 * Created by Luis Palacios on 8/9/17.
 */

class ProductImageAdapter(private val viewModel: ProductViewModel,
                          private val scaleType: ImageView.ScaleType,
                          private val showDescription: Boolean) : androidx.viewpager.widget.PagerAdapter() {

    override fun getCount(): Int {
        return viewModel.itemThumbnail.value?.mImageUrls?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil.inflate<AdapterProductImageItemBinding>(
                LayoutInflater.from(container.context), R.layout.adapter_product_image_item,
                container, false)

        binding.viewModel = viewModel
        binding.productImage.scaleType = scaleType
        binding.position = position
        binding.imageUrl = viewModel.itemThumbnail.value?.mImageUrls?.get(position)

        viewModel.itemThumbnail.value?.mImageDescriptions?.let {
            if(showDescription && it.isNotEmpty()) binding.description = it[position]
        }

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}
