package app.dvkyun.flexhybridand

import android.webkit.JavascriptInterface
import android.webkit.WebView
import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/*
* Available in Java & Kotlin
* */
class FlexJSCall {

    private var mWebView: WebView
    private var mCallable: Callable<*>? = null
    private var oExecutor: ThreadPoolExecutor?
    private var isOutExecutor: Boolean

    constructor(webView: WebView) {
        mWebView = webView
        oExecutor = null
        isOutExecutor = false
    }

    constructor(webView: WebView, executor: ThreadPoolExecutor) {
        mWebView = webView
        oExecutor = executor
        isOutExecutor = true
    }

    fun setExecutor(executor: ThreadPoolExecutor): FlexJSCall {
        oExecutor = executor
        isOutExecutor = true
        return this
    }

    fun setWebView(webView: WebView): FlexJSCall {
        mWebView = webView
        return this
    }

    fun call(callable: Callable<*>): FlexJSCall {
        if(mCallable != null) {
            throw FlexException(FlexException.ERROR7)
        }
        mCallable = callable
        return this
    }

    @JavascriptInterface
    fun job(javascriptFunctionName: String?) {
        if(javascriptFunctionName == null) {
            throw FlexException(FlexException.ERROR2)
        }
        if(mCallable == null) {
            throw FlexException(FlexException.ERROR3)
        }
        if(!isOutExecutor) {
            oExecutor =  ThreadPoolExecutor(
                1,
                2,
                10,
                TimeUnit.SECONDS,
                LinkedBlockingDeque()
            )
        }
        oExecutor?.execute {
            val value = try {
                mCallable?.call()
            } catch (e: Exception) {
                throw FlexException(e)
            } finally {
                mCallable = null
            }
            mWebView.post {
                FlexStatic.evaluateJavaScript(mWebView, "$javascriptFunctionName(\'${value.toString()}\');")
            }
            if(!isOutExecutor) {
                oExecutor?.shutdown()
                oExecutor = null
            }
        }
    }
}