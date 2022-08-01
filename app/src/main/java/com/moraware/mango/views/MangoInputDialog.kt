package com.moraware.mango.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.ARG_POSITIVE_BUTTON
import com.moraware.mango.base.ARG_STRING_TITLE
import com.moraware.mango.base.ARG_TITLE
import com.moraware.mango.databinding.DialogInputBinding

class MangoInputDialog : DialogFragment() {

    interface ICallback {
        fun onInput(input: String)
    }

    companion object {
        const val TAG = "MangoInputDialog"
        const val ARG_FEEDBACK_MSG = "AlertDialog.RequestCode"

        fun newInstanceWithTitle(@StringRes titleResId: Int, @StringRes positiveButtonText: Int, @StringRes feedbackMessage: Int?, callback: ICallback): MangoInputDialog {
            val args = Bundle()
            args.putInt(ARG_TITLE, titleResId)
            args.putInt(ARG_POSITIVE_BUTTON, positiveButtonText)
            if(feedbackMessage != null) args.putInt(ARG_FEEDBACK_MSG, feedbackMessage)
            val fragment = MangoInputDialog()
            fragment.arguments = args
            fragment.dialogCallback = callback
            return fragment
        }
    }

    lateinit var binding: DialogInputBinding
    lateinit var mViewModel: InputViewModel
    private var dialogCallback: ICallback? = null
    private lateinit var feedbackMessage: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_input, null, false)
        binding.lifecycleOwner = this

        val positiveButton = arguments?.getInt(ARG_POSITIVE_BUTTON) as Int
        feedbackMessage = getString(arguments?.getInt(ARG_FEEDBACK_MSG) as Int)

        val builder = AlertDialog.Builder(requireContext())
                .setPositiveButton(positiveButton) { _, _ -> onButtonTapped() }

        val titleResId = arguments?.getInt(ARG_TITLE) as Int
        val titleString: String? = arguments?.getString(ARG_STRING_TITLE)
        if (titleResId > 0) {
            builder.setTitle(titleResId)
        } else if (!titleString.isNullOrEmpty()) {
            builder.setTitle(titleString)
        }
        builder.setView(binding.root)

        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = ViewModelProviders.of(this).get(InputViewModel::class.java)

        mViewModel.validInputEvent.observe(this, Observer {
            dialogCallback?.let {
                dialogCallback?.onInput(mViewModel.input.value ?: "")
                dismiss()
            }
        })

        mViewModel.feedback.value = feedbackMessage

        binding.model = mViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    private fun onButtonTapped() {
        mViewModel.submitInput()
    }
}