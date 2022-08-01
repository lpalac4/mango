package com.moraware.mango.mymeals

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.moraware.domain.models.Meal
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter
import com.moraware.mango.featured.ItemThumbnail


class MyMealsPagerItemAdapter(lifecycleOwner: LifecycleOwner, viewModel: MyMealsViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {
    private var mItems: ArrayList<ItemThumbnail> = ItemThumbnail.fromMeals(viewModel.meals.value)

    init {
        viewModel.meals.observe(lifecycleOwner, Observer {
            updateModels(it)
        })
    }

    private fun updateModels(meals: List<Meal>) {
        mItems.clear()
        mItems.addAll(ItemThumbnail.fromMeals(meals))
        notifyDataSetChanged()
    }

    override fun getObjForPosition(position: Int): Any {
        return mItems[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.adapter_mymeals
    }

    override fun getItemCount(): Int {
        return mItems.size
    }
}