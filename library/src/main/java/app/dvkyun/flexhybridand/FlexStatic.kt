package app.dvkyun.flexhybridand

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.webkit.WebView
import org.json.JSONArray
import kotlin.reflect.typeOf

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

    internal fun convertValue(value: Any?): String {
        return if (value == null) {
            ""
        } else if (value is Int || value is Long || value is Double || value is Float || value is Boolean) {
            "$value"
        } else if (value is String || value is Char) {
            "'${value}'"
        } else if (value is Array<*>) {
            val vString = StringBuilder()
            vString.append("[")
            value.forEach {
                if (it is Int || it is Long || it is Double || it is Float || it is Boolean) {
                    vString.append("${it},")
                } else if (it is String || it is Char) {
                    vString.append("'${it}',")
                } else if (it is Array<*> || it is Iterable<*> || it is Map<*,*>) {
                    vString.append("${convertValue(it)},")
                } else {
                    throw FlexException(FlexException.ERROR9)
                }
            }
            vString.append("]")
            vString.toString()
        } else if (value is Iterable<*>) {
            val vString = StringBuilder()
            vString.append("[")
            value.forEach {
                if (it is Int || it is Long || it is Double || it is Float || it is Boolean) {
                    vString.append("${it},")
                } else if (it is String || it is Char) {
                    vString.append("'${it}',")
                } else if (it is Array<*> || it is Iterable<*> || it is Map<*,*>) {
                    vString.append("${convertValue(it)},")
                } else {
                    throw FlexException(FlexException.ERROR9)
                }
            }
            vString.append("]")
            vString.toString()
        } else if (value is Map<*,*>) {
            val vString = StringBuilder()
            vString.append("{")
            value.forEach {
                if (it.key !is String) {
                    throw FlexException(FlexException.ERROR9)
                }
                if (it.value is Int || it.value  is Long || it.value  is Double || it.value  is Float || it.value  is Boolean) {
                    vString.append("${it.key}:${it.value},")
                } else if (it.value is String || it.value is Char) {
                    vString.append("${it.key}:'${it.value}',")
                } else if (it is Array<*> || it is Iterable<*> || it is Map<*,*>) {
                    vString.append("${it.key}:${convertValue(it.value)},")
                } else {
                    throw FlexException(FlexException.ERROR9)
                }
            }
            vString.append("}")
            vString.toString()
        } else {
            throw FlexException(FlexException.ERROR9)
        }
    }

}