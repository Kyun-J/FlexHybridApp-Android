package app.dvkyun.flexhybridand

class FlexAction(name: String, webView: FlexWebView) {

    private var funName: String = name
    private var flexWebView: FlexWebView = webView

    internal var afterReturn: (() -> Unit)? = null

    fun promiseReturn(response: Any?) {
        if(response == null) {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}()")
        } else {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}(${FlexUtil.convertValue(response)})")
        }
        afterReturn?.invoke()
    }

}