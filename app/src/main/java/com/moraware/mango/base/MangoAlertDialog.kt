package com.moraware.mango.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

const val ARG_TITLE = "AlertDialog.Title"
const val ARG_MESSAGE = "AlertDialog.Message"
const val ARG_STRING_TITLE = "AlertDialog.StringTitle"
const val ARG_STRING_MESSAGE = "AlertDialog.StringMessage"
const val ARG_POSITIVE_BUTTON = "AlertDialog.Positive.Button"
const val ARG_NEGATIVE_BUTTON = "AlertDialog.Negative.Button"
const val ARG_REQUEST_CODE = "AlertDialog.RequestCode"

const val RESULT_POSITIVE = 1
const val RESULT_NEGATIVE = 2

open class MangoAlertDialog : DialogFragment() {

    var dialogCallback: IAlertCallback? = null
    var requestCode: Int = -1

    interface IAlertCallback {
        fun onDialogPositive(requestCode: Int)
        fun onDialogNegative(requestCode: Int)
    }

    companion object {
        const val TAG = "MangoAlertDialog"

        fun newInstanceWithTitle(requestCode: Int, @StringRes titleResId: Int, @StringRes messageResId: Int,
                        @StringRes positiveButtonText: Int, @StringRes negativeButtonText: Int = 0): MangoAlertDialog {
            val args = Bundle()
            args.putInt(ARG_REQUEST_CODE, requestCode)
            args.putInt(ARG_TITLE, titleResId)
            args.putInt(ARG_MESSAGE, messageResId)
            args.putInt(ARG_POSITIVE_BUTTON, positiveButtonText)
            if(negativeButtonText != 0) args.putInt(ARG_NEGATIVE_BUTTON, negativeButtonText)
            val fragment = MangoAlertDialog()
            fragment.arguments = args
            return fragment
        }

        fun newInstanceWithoutTitle(requestCode: Int, title: String, messageResId: String, @StringRes positiveButtonText: Int,
                        @StringRes negativeButtonText: Int): MangoAlertDialog {
            val args = Bundle()
            args.putInt(ARG_REQUEST_CODE, requestCode)
            args.putString(ARG_STRING_TITLE, title)
            args.putString(ARG_STRING_MESSAGE, messageResId)
            args.putInt(ARG_POSITIVE_BUTTON, positiveButtonText)
            args.putInt(ARG_POSITIVE_BUTTON, positiveButtonText)
            if(negativeButtonText != 0) args.putInt(ARG_NEGATIVE_BUTTON, negativeButtonText)
            val fragment = MangoAlertDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val message = getArg(ARG_MESSAGE, ARG_STRING_MESSAGE)
        val positiveButton = arguments?.getInt(ARG_POSITIVE_BUTTON) as Int
        val negativeButton = arguments?.getInt(ARG_NEGATIVE_BUTTON) ?: 0
        requestCode = arguments?.getInt(ARG_REQUEST_CODE) ?: -1

        val builder = AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton(positiveButton) { _, _ -> onButtonTapped(RESULT_POSITIVE) }

        if(negativeButton != 0) builder.setNegativeButton(negativeButton) {_, _ -> onButtonTapped(RESULT_NEGATIVE) }

        val titleResId = arguments?.getInt(ARG_TITLE) as Int
        val titleString: String? = arguments?.getString(ARG_STRING_TITLE)
        if (titleResId > 0) {
            builder.setTitle(titleResId)
        } else if (!titleString.isNullOrEmpty()) {
            builder.setTitle(titleString)
        }

        return builder.create()
    }

    fun getArg(intArg: String, strArg: String): String =
            if (arguments?.containsKey(intArg) == true) {
                getString(arguments?.getInt(intArg) as Int)
            } else {
                arguments?.getString(strArg) as String
            }

    override fun onDetach() {
        super.onDetach()
        dialogCallback = null
    }

    private fun onButtonTapped(buttonResultCode: Int) {
        dialogCallback?.let {
            when (buttonResultCode) {
                RESULT_POSITIVE -> dialogCallback?.onDialogPositive(requestCode)
                RESULT_NEGATIVE -> dialogCallback?.onDialogNegative(requestCode)
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        dialogCallback = targetFragment?.let {
            targetFragment as IAlertCallback
        } ?: context as IAlertCallback
    }
}