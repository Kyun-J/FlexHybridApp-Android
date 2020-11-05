package app.dvkyun.flexhybridand

import kotlinx.coroutines.CoroutineScope

class FlexLambda {
    companion object {
        fun void(lambda: suspend CoroutineScope.(Array<FlexData>) -> Unit?): suspend CoroutineScope.(Array<FlexData>) -> Unit? = lambda
        fun int(lambda: suspend CoroutineScope.(Array<FlexData>) -> Int?): suspend CoroutineScope.(Array<FlexData>) -> Int? = lambda
        fun long(lambda: suspend CoroutineScope.(Array<FlexData>) -> Long?): suspend CoroutineScope.(Array<FlexData>) -> Long? = lambda
        fun float(lambda: suspend CoroutineScope.(Array<FlexData>) -> Float?): suspend CoroutineScope.(Array<FlexData>) -> Float? = lambda
        fun double(lambda: suspend CoroutineScope.(Array<FlexData>) -> Double?): suspend CoroutineScope.(Array<FlexData>) -> Double? = lambda
        fun char(lambda: suspend CoroutineScope.(Array<FlexData>) -> Char?): suspend CoroutineScope.(Array<FlexData>) -> Char? = lambda
        fun string(lambda: suspend CoroutineScope.(Array<FlexData>) -> String?): suspend CoroutineScope.(Array<FlexData>) -> String? = lambda
        fun bool(lambda: suspend CoroutineScope.(Array<FlexData>) -> Boolean?): suspend CoroutineScope.(Array<FlexData>) -> Boolean? = lambda
        fun array(lambda: suspend CoroutineScope.(Array<FlexData>) -> Array<*>?): suspend CoroutineScope.(Array<FlexData>) -> Array<*>? = lambda
        fun list(lambda: suspend CoroutineScope.(Array<FlexData>) -> Iterable<*>?): suspend CoroutineScope.(Array<FlexData>) -> Iterable<*>? = lambda
        fun map(lambda: suspend CoroutineScope.(Array<FlexData>) -> Map<String, *>?): suspend CoroutineScope.(Array<FlexData>) -> Map<String, *>? = lambda
        fun any(lambda: suspend CoroutineScope.(Array<FlexData>) -> Any?): suspend CoroutineScope.(Array<FlexData>) -> Any? = lambda
        fun action(lambda: suspend CoroutineScope.(action: FlexAction, arguments: Array<FlexData>) -> Unit): suspend CoroutineScope.(action: FlexAction, arguments: Array<FlexData>) -> Unit = lambda
    }
}