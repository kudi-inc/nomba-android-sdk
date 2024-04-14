package com.nomba.wraith.ui.shelters.card

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.enums.PaymentOption
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
    var callback : String = "https://checkout.nomba.com/callback/"

    override fun showShelter() {

        super.showShelter()
        setUpValues()
        val postData = "JWT=${URLEncoder.encode(jwtToken, "UTF-8")}&MD=${URLEncoder.encode(md, "UTF-8")}"
        layout().webview.postUrl(acsUrl, postData.toByteArray())
        manager.displayViewState = DisplayViewState.SECURE3DS
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setUpValues(){
        layout().webview.layoutParams = ConstraintLayout.LayoutParams(manager.getWidth() - 20, manager.getHeight())

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout().parentHolder)
        constraintSet.connect(
            layout().webview.id,
            ConstraintSet.START,
            layout().parentHolder.id,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            layout().webview.id,
            ConstraintSet.END,
            layout().parentHolder.id,
            ConstraintSet.END,
            0
        )
        constraintSet.applyTo(layout().parentHolder)

        layout().webview.settings.domStorageEnabled = true
        layout().webview.settings.useWideViewPort = false
        layout().webview.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                layout().webview.evaluateJavascript(
                        "document.addEventListener('DOMContentLoaded', function() { " +
                        "var iframe = document.getElementsByTagName('iframe')[0];" +
                        "var innerFrame = iframe.contentWindow.document;" +
                        // "print(innerFrame);" +
                        "var element = innerFrame.getElementById('ExitLink');" +
                        "element.style.display = 'none';" +
                        "});"
                    ,
                    ValueCallback<String?> {
                        println(it)
                    })
            }
        }

        layout().webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                layout().loader.visibility = View.VISIBLE
                if (url != null) {
                    if (callback in url) {
                        hideShelter()
                        manager.showCardLoadingShelter()
                        manager.checkOrderDetails(paymentOption = PaymentOption.CARD, {})

                    }
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                layout().webview.loadUrl("javascript:document.body.style.margin='8%'; void 0");
                layout().webview.evaluateJavascript("(function(){" +
                        "document.addEventListener('DOMContentLoaded', function() { " +
                        "var iframe = document.getElementsByTagName('iframe')[0];" +
                        "var innerFrame = iframe.contentWindow.document;" +
                        // "print(innerFrame);" +
                        "var element = innerFrame.getElementById('ExitLink');" +
                        "element.style.display = 'none';" +
                        "});" +
                        "})();"
                        ,
                    ValueCallback<String?> {
                        println(it)
                    })
                layout().loader.visibility = View.GONE
            }
        }
        layout().webview.settings.javaScriptEnabled = true
        layout().webview.settings.javaScriptCanOpenWindowsAutomatically = true
        layout().webview.settings.setPluginState(WebSettings.PluginState.ON);
        layout().webview.settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }


}