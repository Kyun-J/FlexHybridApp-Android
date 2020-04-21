package app.dvkyun.flexhybridand.demo

import android.webkit.JavascriptInterface
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.FlexJSAsync
import app.dvkyun.flexhybridand.FlexWebView
import kotlinx.coroutines.*


class JSInterfaceKotlin(webView: FlexWebView) {

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
            .launch(mScope.async {
                AlertDialog.Builder(mWebView.context)
                    .setTitle("Received by WebView")
                    .setMessage(msg)
                    .create()
                    .show()
            })
    }
}