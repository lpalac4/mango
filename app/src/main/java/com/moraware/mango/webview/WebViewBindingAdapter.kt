package com.moraware.mango.webview

import android.webkit.WebView
import androidx.databinding.BindingAdapter

class WebViewBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("assetPath", "url")
        fun loadWebView(webView: WebView, fileName: String, url: String) {
            if(fileName.isNotEmpty()) {
                webView.loadUrl("file:///android_asset/$fileName")
            } else if(url.isNotEmpty()) {
                webView.loadUrl(url)
            }
        }
    }
}