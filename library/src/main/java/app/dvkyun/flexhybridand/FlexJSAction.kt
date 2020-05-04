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

    @JavascriptInterface
    fun job(javascriptFunctionName: String?) {
        if(javascriptFunctionName == null) {
            throw FlexException(FlexException.ERROR2)
        }
        jsFunctionName = javascriptFunctionName
        readyListener?.invoke()
    }

    fun promiseReturn(value: Any?) {
        if(jsFunctionName == null) {
            readyListener = {
                readyListener = null
                mWebView.post {
                    FlexStatic.evaluateJavaScript(mWebView, "$jsFunctionName(${FlexStatic.convertValue(value)});")
                    this.jsFunctionName = null
                }
            }
        } else {
            mWebView.post {
                FlexStatic.evaluateJavaScript(mWebView, "$jsFunctionName(${FlexStatic.convertValue(value)});")
                this.jsFunctionName = null
            }
        }
    }

}