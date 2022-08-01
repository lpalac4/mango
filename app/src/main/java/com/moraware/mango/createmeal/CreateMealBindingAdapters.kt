package com.moraware.mango.createmeal

import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.moraware.mango.R
import com.moraware.mango.databinding.CreateMealRecipeIngredientItemBinding
import com.moraware.mango.databinding.ImageViewBindingAdapter

class CreateMealBindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("model", "container")
        fun setOnClick(view: MaterialButton, model: CreateMealViewModel, container: LinearLayout) {
            view.setOnClickListener {
                val binding = DataBindingUtil.inflate<CreateMealRecipeIngredientItemBinding>(LayoutInflater.from(view.context), R.layout.create_meal_recipe_ingredient_item, container,false)
                val index = model.getNewIngredientId()
                binding.index = index
                binding.model = model

                binding.createMealRecipeRemoveBtn.setOnClickListener {
                    model.onRemoveIngredient(index)
                    container.removeView(binding.root)
                    container.invalidate()
                }

                val newView = binding.root
                container.addView(newView)
                newView.requestFocus()
            }

        }

        @JvmStatic
        @BindingAdapter("photoImage")
        fun setPhotoImage(imageView: ImageView, androidUri: Uri?) {
            if(androidUri == null) {
                imageView.setImageDrawable(null)
            } else ImageViewBindingAdapter.loadCircularImage(imageView, androidUri)
        }
    }
}
