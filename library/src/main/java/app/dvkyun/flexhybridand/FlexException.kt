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
        const val ERROR4 = "" // 들어올수 없는 타입이 들어왔씁니
        const val ERROR5 = "FlexWebView to run javascript is null."
        const val ERROR6 = "BaseUrl cannot be changed after initialization."
        const val ERROR7 = "You can only add interfaces before loading the first page of FlexWebView."
        const val ERROR8 = "An interface or action with the same name has already been added."
        const val ERROR9 = "You cannot set the interface or action name with 'flex'."
    }
}