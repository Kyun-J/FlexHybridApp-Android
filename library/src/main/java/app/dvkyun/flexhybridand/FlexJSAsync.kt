package app.dvkyun.flexhybridand

import android.webkit.JavascriptInterface
import kotlinx.coroutines.*

/*
* Kotlin Only
* */
class FlexJSAsync {

    private var mWebView: FlexWebView
    private var mDeferred: Deferred<*>? = null
    private lateinit var mScope: CoroutineScope

    constructor() {
        FlexStatic.checkGlobalFlexWebView()
        mWebView = FlexStatic.globalFlexWebView
    }

    constructor(webView: FlexWebView) {
        mWebView = webView
    }

    constructor(webView: FlexWebView, scope: CoroutineScope) {
        mWebView = webView
        mScope = scope
    }

    fun setScope(scope: CoroutineScope): FlexJSAsync  {
        mScope = scope
        return this
    }

    fun setWebView(webView: FlexWebView): FlexJSAsync {
        mWebView = webView
        return this
    }

    fun launch(deferred: Deferred<*>): FlexJSAsync {
        if(mDeferred != null) {
            throw FlexException(FlexException.ERROR7)
        }
        mDeferred = deferred
        return this
    }

    @JavascriptInterface
    fun job(javascriptFunctionName: String?) {
        if(javascriptFunctionName == null) {
            throw FlexException(FlexException.ERROR2)
        }
        if(mDeferred == null) {
            throw FlexException(FlexException.ERROR4)
        }
        if(!::mScope.isInitialized) {
            mScope = CoroutineScope(Dispatchers.IO)
        }
        mScope.launch {
            val value = try {
                mDeferred?.await()
            } catch (e: Exception) {
                throw FlexException(e)
            } finally {
                mDeferred = null
            }
            launch(Dispatchers.Main) {
                FlexStatic.evaluateJavaScript(mWebView, "$javascriptFunctionName(${FlexStatic.convertValue(value)});")
            }
        }
    }
}