package com.nomba.wraith.ui.shelters.card

import android.annotation.SuppressLint
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.ThreedsViewBinding
import java.net.URLEncoder


class ThreeDSShelter(var manager: NombaManager, activityThreedsViewBinding: ThreedsViewBinding) : Shelter(activityThreedsViewBinding) {

    override fun layout(): ThreedsViewBinding {
        return super.layout() as ThreedsViewBinding
    }

    var acsUrl : String = ""
    var jwtToken : String = ""
    var md : String = ""
    var termUrl : String = ""


    override fun showShelter() {
        super.showShelter()
        setUpValues()
        val postData = "JWT=${URLEncoder.encode(jwtToken, "UTF-8")}" + "&MD=${URLEncoder.encode(md, "UTF-8")}"
        layout().webview.postUrl(acsUrl, postData.toByteArray())
    }
    @SuppressLint("SetJavaScriptEnabled")
    fun setUpValues(){

        // layout().webview.settings.loadWithOverviewMode = true
        layout().webview.settings.domStorageEnabled = true
        layout().webview.settings.useWideViewPort = true
        val agent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"
        layout().webview.settings.userAgentString = agent

        layout().webview.webChromeClient = WebChromeClient()
        layout().webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // do your stuff here
                println(url)
//                layout().webview.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
//                    ValueCallback<String?> {
//                        println(it)
//                    })
                layout().loader.visibility = View.GONE
            }
        }
        layout().webview.settings.javaScriptEnabled = true
        layout().webview.settings.javaScriptCanOpenWindowsAutomatically = true
        layout().webview.settings.setSupportMultipleWindows(true);
        layout().webview.settings.setPluginState(WebSettings.PluginState.ON);
        layout().webview.settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }


}