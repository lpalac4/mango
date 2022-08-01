package com.moraware.mango.databinding

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.lang.Long.parseLong

/**
 * Created by luis palacios on 7/30/17.
 */
@GlideModule
object ImageViewBindingAdapter : AppGlideModule() {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, uri: String?) {
        Glide.with(view.context).load(uri)
                .placeholder(android.R.color.holo_green_light)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(android.R.color.holo_red_light)
                .fallback(android.R.color.holo_orange_light).into(view)
    }

    @JvmStatic
    @BindingAdapter("gifId")
    fun loadGif(view: ImageView, gifId: Int) {
        Glide.with(view.context).load(gifId).transition(DrawableTransitionOptions.withCrossFade()).into(view)
    }

    @JvmStatic
    @BindingAdapter("imageUrl", "placeholder")
    fun loadImage(view: ImageView, url: String?, placeholder: String) {
        try {
            val colorInt = ColorDrawable(parseLong(placeholder, 16).toInt())
            Glide.with(view.context).load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions().placeholder(colorInt))
                    .into(view)
        } catch (e : NumberFormatException) {
            Glide.with(view.context).load(url)
                    .placeholder(android.R.color.holo_green_light)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(android.R.color.holo_red_light)
                    .fallback(android.R.color.holo_orange_light).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("circularImageUri")
    fun loadCircularImage(view: ImageView, uri: Uri?) {
        Glide.with(view.context).load(uri).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(view)
    }

    @JvmStatic
    @BindingAdapter("circularImageId")
    fun loadCircularImage(view: ImageView, image: Int) {
        Glide.with(view.context).load(image).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(view)
    }

    @JvmStatic
    @BindingAdapter("circularImageUrl", "circularImageSrc")
    fun loadCircularImageFromUrlFirstThenDrawable(view: ImageView, imageUrl: String, image: Drawable) {
        if(imageUrl.isNotEmpty()) Glide.with(view.context).load(imageUrl).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(view)
        else Glide.with(view.context).load(image).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(view)
    }

    @JvmStatic
    @BindingAdapter("circularImageUrl")
    fun loadCircularImage(view: ImageView, imageUrl: String) {
        if(imageUrl.isNotEmpty()) Glide.with(view.context).load(imageUrl).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(view)
    }
}
