package app.dvkyun.flexhybridand

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.webkit.ValueCallback
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.Reader

internal object FlexUtil {

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

    internal fun evaluateJavaScript(webView: WebView?, javascript: String) {
        if(webView == null) {
            throw FlexException(FlexException.ERROR4)
        }
        webView.post {
            val js = "javascript:$javascript; void 0;"
            if (Build.VERSION.SDK_INT >= 19) {
                webView.evaluateJavascript(js,null)
            } else {
                webView.loadUrl(js)
            }
        }
    }

    internal fun convertJSONArray(value: JSONArray): Array<Any?> {
        return Array(value.length())
        { i ->
            val element = value[i]
            if (element is Int || element is Double || element is Boolean || element is String) {
                element
            } else if (element is JSONArray) {
                convertJSONArray(element)
            } else if (element is JSONObject) {
                convertJSONObject(element)
            } else if (element == null) {
                null
            } else {
                throw FlexException(FlexException.ERROR3)
            }
        }
    }

    internal fun convertJSONObject(value: JSONObject): Map<*,*> {
        val result = HashMap<String, Any?>()
        value.keys().forEach {
            if (value.isNull(it)) {
                result[it] = null
            } else {
                val element = value[it]
                if (element is Int || element is Double || element is Boolean || element is String) {
                    result[it] = element
                } else if (element is JSONArray) {
                    result[it] = convertJSONArray(element)
                } else if (element is JSONObject) {
                    result[it] = convertJSONObject(element)
                } else {
                    throw FlexException(FlexException.ERROR3)
                }
            }
        }
        return result
    }

    internal fun convertInput(value: Any?): String {
        return if (value is Int || value is Long || value is Double || value is Float || value is Boolean) {
            "$value"
        } else if (value is String || value is Char) {
            "`${value}`"
        } else if (value is Array<*>) {
            val vString = StringBuilder()
            vString.append("[")
            value.forEach {
                if (it == null) {
                    vString.append("null,")
                } else if (it is Int || it is Long || it is Double || it is Float || it is Boolean) {
                    vString.append("${it},")
                } else if (it is String || it is Char) {
                    vString.append("`${it}`,")
                } else if (it is Array<*> || it is Iterable<*> || it is Map<*,*> || it is JSONArray || it is JSONObject) {
                    vString.append("${convertInput(it)},")
                } else {
                    throw FlexException(FlexException.ERROR3)
                }
            }
            vString.append("]")
            vString.toString()
        } else if (value is Iterable<*>) {
            val vString = StringBuilder()
            vString.append("[")
            value.forEach {
                if (it == null) {
                    vString.append("null,")
                } else if (it is Int || it is Long || it is Double || it is Float || it is Boolean) {
                    vString.append("${it},")
                } else if (it is String || it is Char) {
                    vString.append("`${it}`,")
                } else if (it is Array<*> || it is Iterable<*> || it is Map<*,*> || it is JSONArray || it is JSONObject) {
                    vString.append("${convertInput(it)},")
                } else {
                    throw FlexException(FlexException.ERROR3)
                }
            }
            vString.append("]")
            vString.toString()
        } else if (value is JSONArray) {
            val vString = StringBuilder()
            vString.append("[")
            for(i in 0 until value.length()) {
                val element = value[i]
                if (element == null) {
                    vString.append("null,")
                } else if (element is Int || element is Long || element is Double || element is Float || element is Boolean) {
                    vString.append("${element},")
                } else if (element is String || element is Char) {
                    vString.append("`${element}`,")
                } else if (element is Array<*> || element is Iterable<*> || element is Map<*,*> || element is JSONArray || element is JSONObject) {
                    vString.append("${convertInput(element)},")
                } else {
                    throw FlexException(FlexException.ERROR3)
                }
            }
            vString.append("]")
            vString.toString()
        } else if (value is Map<*,*>) {
            val vString = StringBuilder()
            vString.append("{")
            value.forEach {
                if (it.key !is String) {
                    throw FlexException(FlexException.ERROR3)
                }
                if (it.value == null) {
                    vString.append("${it.key}: null,")
                } else if (it.value is Int || it.value is Long || it.value is Double || it.value is Float || it.value is Boolean || it.value == null) {
                    vString.append("${it.key}:${it.value},")
                } else if (it.value is String || it.value is Char) {
                    vString.append("${it.key}:`${it.value}`,")
                } else if (it.value is Array<*> || it.value is Iterable<*> || it.value is Map<*,*> || it.value is JSONArray || it.value is JSONObject) {
                    vString.append("${it.key}:${convertInput(it.value!!)},")
                } else {
                    throw FlexException(FlexException.ERROR3)
                }
            }
            vString.append("}")
            vString.toString()
        } else if (value is JSONObject) {
            val vString = StringBuilder()
            vString.append("{")
            value.keys().forEach {
                if(value.isNull(it)) {
                    vString.append("${it}: null,")
                } else {
                    val element = value[it]
                    if (element is Int || element  is Long || element  is Double || element is Float || element  is Boolean) {
                        vString.append("${it}:${element},")
                    } else if (element is String || element is Char) {
                        vString.append("${it}:`${element}`,")
                    } else if (element is Array<*> || element is Iterable<*> || element is Map<*,*> || element is JSONArray || element is JSONObject) {
                        vString.append("${it}:${convertInput(element)},")
                    } else {
                        throw FlexException(FlexException.ERROR3)
                    }
                }
            }
            vString.append("}")
            vString.toString()
        } else if(value == null) {
            "null"
        } else {
            throw FlexException(FlexException.ERROR3)
        }
    }

    internal fun jsonArrayToFlexData(value: JSONArray?): Array<FlexData> {
        if(value == null) return emptyArray()
        val res = Array(value.length()) { FlexData() }
        for(i in 0 until value.length()) {
            when(val ele = value[i]) {
                is Int -> res[i] = FlexData(ele)
                is String -> res[i] = FlexData(ele)
                is Long -> res[i] = FlexData(ele)
                is Double -> res[i] = FlexData(ele)
                is Boolean -> res[i] = FlexData(ele)
                is JSONArray -> res[i] = FlexData(jsonArrayToFlexData(ele))
                is JSONObject -> res[i] = FlexData(jsonObjectToFlexData(ele))
            }
        }
        return res
    }

    internal fun jsonObjectToFlexData(value: JSONObject?): Map<String, FlexData> {
        val res = HashMap<String, FlexData>()
        if(value == null) return res
        value.keys().forEach {
            when(val ele = value[it]) {
                is Int -> res[it] = FlexData(ele)
                is String -> res[it] = FlexData(ele)
                is Long -> res[it] = FlexData(ele)
                is Double -> res[it] = FlexData(ele)
                is Boolean -> res[it] = FlexData(ele)
                is JSONArray -> res[it] = FlexData(jsonArrayToFlexData(ele))
                is JSONObject -> res[it] = FlexData(jsonObjectToFlexData(ele))
                else -> res[it] = FlexData()
            }
        }
        return res
    }

    internal fun anyToFlexData(value: Any?): FlexData {
        return when(value) {
            is Int -> FlexData(value)
            is String -> FlexData(value)
            is Long -> FlexData(value)
            is Double -> FlexData(value)
            is Boolean -> FlexData(value)
            is JSONArray -> FlexData(jsonArrayToFlexData(value))
            is JSONObject -> FlexData(jsonObjectToFlexData(value))
            is BrowserException -> FlexData(value)
            else -> FlexData()
        }
    }

    private const val TAG = "FLEXHYBRID"

    internal fun INFO(msg: Any?) {
        android.util.Log.i(TAG, msg.toString())
    }

    internal fun DEBUG(msg: Any?) {
        android.util.Log.d(TAG, msg.toString())
    }

    internal fun VERBOSE(msg: Any?) {
        android.util.Log.v(TAG, msg.toString())
    }

    internal fun WARN(msg: Any?) {
        android.util.Log.w(TAG, msg.toString())
    }

    internal fun ERR(msg: Any?) {
        android.util.Log.e(TAG, msg.toString())
    }


    internal fun fileToString(inputStream: InputStream): String {
        try {
            val bufferedReader = BufferedReader(inputStream.reader())
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            bufferedReader.close()
            return sb.toString()
        } catch (e: java.lang.Exception) {
            throw FlexException(e)
        }
    }

    internal fun fileToString(reader: Reader): String {
        try {
            val bufferedReader = BufferedReader(reader)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            bufferedReader.close()
            return sb.toString()
        } catch (e: java.lang.Exception) {
            throw FlexException(e)
        }
    }

}