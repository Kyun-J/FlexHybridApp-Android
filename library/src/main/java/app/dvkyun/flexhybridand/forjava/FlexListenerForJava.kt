package app.dvkyun.flexhybridand.forjava

import app.dvkyun.flexhybridand.FlexEvent

interface FlexListenerForJava {
    fun onEvent(type: FlexEvent, url: String, funcName: String, msg: String)
}