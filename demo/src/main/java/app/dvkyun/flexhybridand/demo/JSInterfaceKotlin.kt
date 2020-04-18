package app.dvkyun.flexhybridand.demo

import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.FlexJSAsync
import kotlinx.coroutines.*


class JSInterfaceKotlin(webView: WebView) {

    private val mWebView = webView
    private val mScope = CoroutineScope(Dispatchers.Main)

    @JavascriptInterface
    fun testAsync(msg: String) : FlexJSAsync {
        return FlexJSAsync()
            .setScope(mScope)
            .launch(mScope.async {
                AlertDialog.Builder(mWebView.context)
                    .setTitle("Received by WebView")
                    .setMessage(msg)
                    .create()
                    .show()
            })
    }

    @JavascriptInterface
    fun testAsync2(msg: String) : FlexJSAsync {
        return FlexJSAsync(mWebView)
            .setScope(mScope)
            .launch(mScope.async {
                AlertDialog.Builder(mWebView.context)
                    .setTitle("Received by WebView")
                    .setMessage(msg)
                    .create()
                    .show()
            })
    }
}