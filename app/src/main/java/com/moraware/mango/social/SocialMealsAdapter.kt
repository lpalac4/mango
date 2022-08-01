package com.moraware.mango.social

import com.moraware.domain.models.Meal
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter

class SocialMealsAdapter(viewModel: SocialViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {

    private var mealList : List<Meal> = viewModel.meals.value ?: emptyList()

    fun updateModels(meals: List<Meal>?) {
        mealList = meals ?: emptyList()
        notifyDataSetChanged()
    }

    override fun getObjForPosition(position: Int): Any {
        return mealList[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.adapter_social_meal_item
    }

    override fun getItemCount(): Int {
        return mealList.size
    }
}