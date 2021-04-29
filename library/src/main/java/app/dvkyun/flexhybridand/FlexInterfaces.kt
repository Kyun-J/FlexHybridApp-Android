package app.dvkyun.flexhybridand

import app.dvkyun.flexhybridand.forjava.*
import kotlinx.coroutines.CoroutineScope

open class FlexInterfaces {

    internal val interfaces: HashMap<String, suspend CoroutineScope.(FlexArguments) -> Any?> = HashMap()
    internal val actions: HashMap<String, suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit> = HashMap()
    internal val iTimeouts: HashMap<String, Int> = HashMap()

    private fun setInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(FlexArguments) -> Any?): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains(FlexWebView.FLEX)) {
            throw FlexException(FlexException.ERROR8)
        }
        timeout?.also {
            iTimeouts[name] = if(it != 0 && it < 100) 100 else it
        }
        interfaces[name] = lambda
        return this
    }

    fun voidInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Unit): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun stringInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> String?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun intInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Int?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun charInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Char?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun longInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Long?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun doubleInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Double?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun floatInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Float?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun boolInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Boolean?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun arrayInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Array<*>?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun listInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Iterable<*>?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun mapInterface(name: String, timeout: Int? = null, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Map<String, *>?): FlexInterfaces {
        return setInterface(name, timeout, lambda)
    }

    fun setAction(name: String, timeout: Int? = null, action: suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        timeout?.also {
            iTimeouts[name] = if(it != 0 && it < 100) 100 else it
        }
        actions[name] = action
        return this
    }


    fun voidInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlexVoid): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun stringInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<String>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun intInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Int>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun charInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Char>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun longInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Long>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun doubleInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Double>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun floatInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Float>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun boolInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Boolean>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun arrayInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Array<*>>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun listInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Iterable<*>>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun mapInterfaceForJava(name: String, timeout: Int?, invoke: InvokeFlex<Map<String, *>>): FlexInterfaces {
        return setInterface(name, timeout) { args -> invoke.invoke(args) }
    }

    fun setActionForJava(name: String, timeout: Int?, invoke: InvokeAction): FlexInterfaces {
        return setAction(name, timeout) { ac, args -> invoke.invoke(ac, args) }
    }

}