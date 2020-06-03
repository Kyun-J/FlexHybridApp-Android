package app.dvkyun.flexhybridand

class FlexAction(name: String, webView: FlexWebView) {

    private var funName: String = name
    private var flexWebView: FlexWebView = webView
    private var isCall = false


    fun promiseReturn(response: Any?) {
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