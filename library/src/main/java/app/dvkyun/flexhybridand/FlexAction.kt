package app.dvkyun.flexhybridand

class FlexAction(action :(self: FlexAction, arguments: Array<Any?>?) -> Unit) {

    internal val doAction: (self: FlexAction, arguments: Array<Any?>?) -> Unit = action
    private var funName: String? = null
    private var onReady: (() -> Unit)? = null
    internal var flexWebView: FlexWebView? = null

    internal fun setFunName(name: String) {
        funName = name
        onReady?.invoke()
    }

    private fun action(response: Any?) {
        if(response == null) {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}()")
        } else {
            FlexUtil.evaluateJavaScript(flexWebView,"window.${funName}(${FlexUtil.convertValue(response)})")
        }
        funName = null
        flexWebView = null
    }

    fun promiseReturn(response: Any?) {
        if(funName != null) {
            action(response)
        } else {
            onReady = {
                onReady = null
                action(response)
            }
        }
    }

}