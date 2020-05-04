package app.dvkyun.flexhybridand

import java.lang.Exception

class FlexException : Exception {

    constructor(msg : String) : super(msg)
    constructor(e: Throwable): super(e)
    constructor(msg : String, e : Throwable) : super(msg,e)

    companion object {
        const val ERROR1 = "Context must be Activity Context."
        const val ERROR2 = "BaseUrl is not set. Please specify BaseUrl first"
        const val ERROR3 = "ViewClient or ChromeClient must be FlexViewClient or FlexChromeClient"
        const val ERROR4 = "Static Global Variable is not initialized."
        const val ERROR5 = "" // 들어올수 없는 타입이 들어왔씁니
        const val ERROR6 = "FlexWebView to run javascript is null."
        const val ERROR7 = "BaseUrl cannot be changed after initialization."
    }
}