package com.moraware.mango.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelFragment
import com.moraware.mango.databinding.FragmentWebviewBinding

class WebViewFragment: ViewModelFragment<WebViewViewModel>() {

    private lateinit var callback: () -> Unit
    lateinit var binding: FragmentWebviewBinding

    companion object {
        const val ARGS_URL = "MangoWebViewFragment.args.url"
        const val ARGS_ASSET = "MangoWebViewFragment.args.asset"
        const val ARG_REQUIRE_CONFIRMATION = "MangoWebViewFragment.arg.require_confirmation"

        val TAG: String = WebViewFragment::class.java.simpleName

        fun newInstanceForUrl(url: String, requireConfirmation: Boolean) : WebViewFragment {
            val args = Bundle()
            args.putString(ARGS_URL, url)
            args.putBoolean(ARG_REQUIRE_CONFIRMATION, requireConfirmation)
            return WebViewFragment().apply { arguments = args }
        }

        fun newInstanceForAsset(path: String, requireConfirmation: Boolean) : WebViewFragment {
            val args = Bundle()
            args.putString(ARGS_ASSET, path)
            args.putBoolean(ARG_REQUIRE_CONFIRMATION, requireConfirmation)
            return WebViewFragment().apply { arguments = args }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(WebViewViewModel::class.java)
        binding.viewModel = mModel

        arguments?.getString(ARGS_ASSET)?.let {
            mModel.initForAssetPath(it)
        }

        arguments?.getString(ARGS_URL)?.let {
            mModel.initForUrl(it)
        }

        arguments?.getBoolean(ARG_REQUIRE_CONFIRMATION)?.let {
            mModel.setRequiresAccept(it)
        }

        mModel.onActionTakeEvent.observe(this, Observer {
            callback()
        })
    }

    fun onLoadNewBundle(isAsset: Boolean, pathOrUrl: String, requireConfirmation: Boolean) {
        if(isAsset) {
            mModel.initForAssetPath(pathOrUrl)
        } else {
            mModel.initForUrl(pathOrUrl)
        }

        mModel.setRequiresAccept(requireConfirmation)
    }

    fun addConfirmCallback(function: () -> Unit) {
        callback = function
    }
}