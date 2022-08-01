package com.moraware.mango.createmeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moraware.mango.R
import com.moraware.mango.databinding.DialogCreateMealPhotoSourceBinding

/**
 * Created by Luis Palacios on 8/8/18.
 */
class PhotoSourceDialog : BottomSheetDialogFragment() {

    lateinit var binding: DialogCreateMealPhotoSourceBinding
    lateinit var callback: IPhotoSourceDialogCallback
    private var identifier = -1

    interface IPhotoSourceDialogCallback {
        fun onCameraAsSource()
        fun onGalleryAsSource()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme_Dialog)
        val inflaterWithTheme = inflater.cloneInContext(contextThemeWrapper)
        binding = DataBindingUtil.inflate(inflaterWithTheme, R.layout.dialog_create_meal_photo_source, container, false)
        binding.photoSourceCameraBtn.setOnClickListener {
            callback.onCameraAsSource()
            dismiss()
        }

        binding.photoSourceGalleryBtn.setOnClickListener {
            callback.onGalleryAsSource()
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        identifier = arguments?.getInt(IDENTIFIER_ARG) ?: -1
    }

    companion object {
        const val IDENTIFIER_ARG = "CreateMealActivity.identifier"
    }
}