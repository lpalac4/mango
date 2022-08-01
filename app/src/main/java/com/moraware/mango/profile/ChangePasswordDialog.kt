package com.moraware.mango.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.MangoAlertDialog
import com.moraware.mango.databinding.DialogChangePasswordBinding

class ChangePasswordDialog: MangoAlertDialog() {

    companion object {
        const val TAG = "ChangePasswordDialog"
    }

    lateinit var binding: DialogChangePasswordBinding
    lateinit var mViewModel: ChangePasswordViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_change_password, null, false)
        binding.lifecycleOwner = this

        mViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel::class.java)
        binding.model = mViewModel

        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.change_password_dialog_title)
                .setView(binding.root)
                .setPositiveButton(R.string.change_password_confirm) { _, _ -> } // dont dismiss leave empty
                .setNegativeButton(R.string.dialog_button_cancel) { _, _ -> }
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.changePasswordSuccessEvent.observe(this, Observer {
            Toast.makeText(context, R.string.change_password_success, Toast.LENGTH_LONG).show()
            dismiss()
        })
    }

    override fun onStart() {
        super.onStart()
        mViewModel.loadData()

        // this bug is so old!!!
        (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)?.setOnClickListener {
            mViewModel.changePassword()
        }
    }
}