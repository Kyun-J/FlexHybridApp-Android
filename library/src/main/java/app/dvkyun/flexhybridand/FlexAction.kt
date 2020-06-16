package app.dvkyun.flexhybridand

import org.json.JSONArray
import org.json.JSONObject

class FlexAction(name: String, webView: FlexWebView) {

    private var funName: String = name
    private var flexWebView: FlexWebView = webView
    private var isCall = false

    private fun pReturn(response: Any?) {
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        if(response is FlexReject) {
            val reason = if(response.reason == null) null else "\"${response.reason}\""
            FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(false, ${reason})")
        } else if(response == null || response == Unit) {
            FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(true)")
        } else {
            FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(true, null, ${FlexUtil.convertValue(response)})")
        }
    }

    fun promiseReturn(response: String) {
        pReturn(response)
    }

    fun promiseReturn(response: Int) {
        pReturn(response)
    }

    fun promiseReturn(response: Long) {
        pReturn(response)
    }

    fun promiseReturn(response: Float) {
        pReturn(response)
    }

    fun promiseReturn(response: Double) {
        pReturn(response)
    }

    fun promiseReturn(response: Char) {
        pReturn(response)
    }

    fun promiseReturn(response: JSONArray) {
        pReturn(response)
    }

    fun promiseReturn(response: Array<*>) {
        pReturn(response)
    }

    fun promiseReturn(response: Iterable<*>) {
        pReturn(response)
    }

    fun promiseReturn(response: JSONObject) {
        pReturn(response)
    }

    fun promiseReturn(response: Map<String, *>) {
        pReturn(response)
    }

    fun promiseReturn(response: Unit) {
        pReturn(response)
    }

    fun promiseReturn(response: Void) {
        pReturn(response)
    }

    fun promiseReturn(response: FlexReject) {
        pReturn(response)
    }

    fun promiseReturn() {
        pReturn(null)
    }

    fun resolveVoid() {
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(true)")
    }

    fun reject(reason: FlexReject) {
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        val rejectReason = if(reason.reason == null) null else "\"${reason.reason}\""
        FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(false, ${rejectReason})")
    }

    fun reject(reason: String) {
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(false, \"${reason}\")")
    }

    fun reject() {
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(false)")
    }

}