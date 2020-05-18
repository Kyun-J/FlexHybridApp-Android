package app.dvkyun.flexhybridand

import java.lang.Exception

internal class FlexException : Exception {

    constructor(msg : String) : super(msg)
    constructor(e: Throwable): super(e)
    constructor(msg : String, e : Throwable) : super(msg,e)

    internal companion object {
        const val ERROR1 = "Context must be Activity Context."
        const val ERROR2 = "ViewClient or ChromeClient must be FlexViewClient or FlexChromeClient"
        const val ERROR3 = "Only Web interfaces with Int, DOUBLE, Float, Boolean, Char, String, JSONObject, JSONArray, Array <Any> (Object [], Iterable <Any> (like ArrayList), and Map>String,Any> (like HashMap) data are available."
        const val ERROR4 = "FlexWebView to run javascript is null."
        const val ERROR5 = "BaseUrl cannot be changed after initialization."
        const val ERROR6 = "Interface can only be added before the first page of FlexWebView is loaded."
        const val ERROR7 = "An interface or action with the same name has already been added."
        const val ERROR8 = "Interface or task name cannot be set to 'flex'."
        const val ERROR9 = "PromiseReturn cannot be called twice in a single FlexAction."
        const val ERROR10 = "FlexFuncInterface can only have one parameter of JSONArray."
        const val ERROR11 = "FlexActionInterface can only have 2 parameters in order of FlexAction and JSONArray."
        const val ERROR12 = "FlexFuncInterface and FlexActionInterface must be public."
        const val ERROR13 = "The BaseUrl can only use file://, http://, https:// protocols."
    }
}