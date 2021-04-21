package app.dvkyun.flexhybridand

import kotlinx.coroutines.CoroutineScope

class FlexLambda {
    companion object {
        fun void(lambda: suspend CoroutineScope.(FlexArguments) -> Unit): suspend CoroutineScope.(FlexArguments) -> Unit = lambda
        fun int(lambda: suspend CoroutineScope.(FlexArguments) -> Int?): suspend CoroutineScope.(FlexArguments) -> Int? = lambda
        fun long(lambda: suspend CoroutineScope.(FlexArguments) -> Long?): suspend CoroutineScope.(FlexArguments) -> Long? = lambda
        fun float(lambda: suspend CoroutineScope.(FlexArguments) -> Float?): suspend CoroutineScope.(FlexArguments) -> Float? = lambda
        fun double(lambda: suspend CoroutineScope.(FlexArguments) -> Double?): suspend CoroutineScope.(FlexArguments) -> Double? = lambda
        fun char(lambda: suspend CoroutineScope.(FlexArguments) -> Char?): suspend CoroutineScope.(FlexArguments) -> Char? = lambda
        fun string(lambda: suspend CoroutineScope.(FlexArguments) -> String?): suspend CoroutineScope.(FlexArguments) -> String? = lambda
        fun bool(lambda: suspend CoroutineScope.(FlexArguments) -> Boolean?): suspend CoroutineScope.(FlexArguments) -> Boolean? = lambda
        fun array(lambda: suspend CoroutineScope.(FlexArguments) -> Array<*>?): suspend CoroutineScope.(FlexArguments) -> Array<*>? = lambda
        fun list(lambda: suspend CoroutineScope.(FlexArguments) -> Iterable<*>?): suspend CoroutineScope.(FlexArguments) -> Iterable<*>? = lambda
        fun map(lambda: suspend CoroutineScope.(FlexArguments) -> Map<String, *>?): suspend CoroutineScope.(FlexArguments) -> Map<String, *>? = lambda
        fun any(lambda: suspend CoroutineScope.(FlexArguments) -> Any?): suspend CoroutineScope.(FlexArguments) -> Any? = lambda
        fun action(lambda: suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit): suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit = lambda
    }
}