package com.moraware.mango.profile

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.MangoAlertDialog
import com.moraware.mango.databinding.DialogAboutUsBinding
import kotlinx.android.synthetic.main.activity_create_user.*


class AboutUsDialog: MangoAlertDialog() {

    companion object {
        const val TAG = "AboutUsDialog"
    }

    lateinit var binding: DialogAboutUsBinding
    lateinit var mViewModel: AboutUsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_about_us, null, false)
        binding.lifecycleOwner = this

        mViewModel = ViewModelProviders.of(this).get(AboutUsViewModel::class.java)
        binding.model = mViewModel

        return AlertDialog.Builder(requireContext())
                .setView(binding.root)
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.openTwitterEvent.observe(this, Observer {
            openTwitter()
        })
    }

    private fun openTwitter() {
        var intent: Intent?
        try {
            val username = getString(R.string.twitter_username)
            this.activity?.packageManager?.getPackageInfo("com.twitter.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=$username"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) { // no Twitter app, revert to browser
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$username"))
        }
        this.startActivity(intent)
    }
}
