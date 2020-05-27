package app.dvkyun.flexhybridand

class FlexAction(name: String, webView: FlexWebView) {

    private var funName: String = name
    private var flexWebView: FlexWebView = webView
    private var isCall = false

    internal var afterReturn: (() -> Unit)? = null

    fun promiseReturn(response: Any?) {
        afterReturn?.invoke()
        if(isCall) {
            FlexUtil.INFO(FlexException.ERROR9)
            return
        }
        isCall = true
        if(response == null || response == Unit) {
            FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}()")
        } else {
            FlexUtil.evaluateJavaScript(flexWebView,"\$flex.flex.${funName}(${FlexUtil.convertValue(response)})")
        }
    }

}