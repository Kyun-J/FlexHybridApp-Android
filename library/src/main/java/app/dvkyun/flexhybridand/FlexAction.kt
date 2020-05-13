package app.dvkyun.flexhybridand

class FlexAction(name: String, webView: FlexWebView) {

    private var funName: String = name
    private var flexWebView: FlexWebView = webView
    private var isCall = false

    internal var afterReturn: (() -> Unit)? = null

    fun promiseReturn(response: Any?) {
        afterReturn?.invoke()
        if(isCall) throw FlexException(FlexException.ERROR10)
        isCall = true
        if(response == null || response == Unit) {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}()")
        } else {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}(${FlexUtil.convertValue(response)})")
        }
    }

}