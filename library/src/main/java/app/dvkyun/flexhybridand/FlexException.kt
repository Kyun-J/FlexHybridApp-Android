package app.dvkyun.flexhybridand

import java.lang.Exception

class FlexException : Exception {

    constructor(msg : String) : super(msg)
    constructor(e: Throwable): super(e)
    constructor(msg : String, e : Throwable) : super(msg,e)

    companion object {
        const val ERROR1 = "Context must be Activity Context."
        const val ERROR2 = "The name of the returned javascript function is null. When using FlexJSAction, do not call send before it is ready."
        const val ERROR3 = "Callable Object Cannot be null."
        const val ERROR4 = "Deferred Object Cannot be null."
        const val ERROR5 = "BaseUrl is not set. Please specify BaseUrl first"
        const val ERROR6 = "ViewClient or ChromeClient must be FlexViewClient or FlexChromeClient"
        const val ERROR7 = "Callable or Deferred Job already assigned"
        const val ERROR8 = "Static Global Variable is not initialized."
        const val ERROR9 = ""
    }
}