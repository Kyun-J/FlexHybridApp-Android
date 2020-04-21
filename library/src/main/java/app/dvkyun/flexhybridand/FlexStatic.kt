package app.dvkyun.flexhybridand

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.webkit.WebView

object FlexStatic {

    internal lateinit var globalFlexWebView: FlexWebView

    internal fun checkGlobalFlexWebView() {
        if(!::globalFlexWebView.isInitialized) throw FlexException(FlexException.ERROR8)
    }

    fun getGlobalFlexWebView(): FlexWebView? {
        if(!::globalFlexWebView.isInitialized) return null
        return globalFlexWebView
    }

    internal fun getActivity(context: Context): Activity? {
        if (context is ContextWrapper) {
            return if (context is Activity) {
                context
            } else {
                getActivity(context.baseContext)
            }
        }
        return null
    }

    internal fun evaluateJavaScript(webView: WebView, javascript: String) {
        val js = "javascript:$javascript"
        if (Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(js, null)
        } else {
            webView.loadUrl(js)
        }
    }

}