package app.dvkyun.flexhybridand

import android.webkit.JavascriptInterface

class FlexJSAction {

    private var mWebView: FlexWebView
    private var jsFunctionName: String? = null
    private var readyListener: (() -> Any)? = null

    constructor() {
        FlexStatic.checkGlobalFlexWebView()
        mWebView = FlexStatic.globalFlexWebView
    }

    constructor(webView: FlexWebView) {
        mWebView = webView
    }

    fun setWebView(webView: FlexWebView): FlexJSAction {
        mWebView = webView
        return this
    }

    fun setReadyListener(listener: () -> Any) {
        readyListener = listener
    }

    fun isReady(): Boolean {
        return jsFunctionName != null
    }

    @JavascriptInterface
    fun job(javascriptFunctionName: String?) {
        if(javascriptFunctionName == null) {
            throw FlexException(FlexException.ERROR2)
        }
        jsFunctionName = javascriptFunctionName
        send(readyListener?.invoke())
    }

    fun send(value: Any?) {
        if(jsFunctionName == null) {
            throw FlexException(FlexException.ERROR2)
        }
        mWebView.post {
            FlexStatic.evaluateJavaScript(mWebView, "$jsFunctionName(\'${value.toString()}\');")
        }
    }

}