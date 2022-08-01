package com.moraware.mango.featured

import com.moraware.domain.models.Meal
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter

/**
 * Created by Luis Palacios on 7/28/17.
 */

private const val INFINITE_SCROLLING_OFFSET = 5
class FeaturedAdapter(viewModel: FeaturedViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel, INFINITE_SCROLLING_OFFSET) {

    private var mItems : ArrayList<ItemThumbnail> = ItemThumbnail.fromMeals(viewModel.mFeaturedMeals.value)

    fun updateModels(meals: List<Meal>) {
        if(mItems.size == meals.size) return

        mItems.clear()
        mItems.addAll(ItemThumbnail.fromMeals(meals))
        notifyDataSetChanged()
    }

    override fun getObjForPosition(position: Int): Any {
        return mItems[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.adapter_featured_item
    }

    override fun getItemCount(): Int {
        return mItems.size
    }
}
