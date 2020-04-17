package app.dvkyun.flexhybrid

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.webkit.WebView

internal object FlexStatic {

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