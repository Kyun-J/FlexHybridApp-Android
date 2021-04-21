package app.dvkyun.flexhybridand

import app.dvkyun.flexhybridand.forjava.*
import kotlinx.coroutines.CoroutineScope

open class FlexInterfaces {

    internal val interfaces: HashMap<String, suspend CoroutineScope.(FlexArguments) -> Any?> = HashMap()
    internal val actions: HashMap<String, suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit> = HashMap()

    private fun setInterface(name: String, lambda: suspend CoroutineScope.(FlexArguments) -> Any?): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        interfaces[name] = lambda
        return this
    }

    fun voidInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Unit): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun stringInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> String?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun intInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Int?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun charInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Char?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun longInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Long?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun doubleInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Double?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun floatInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Float?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun boolInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Boolean?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun arrayInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Array<*>?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun listInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Iterable<*>?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun mapInterface(name: String, lambda: suspend CoroutineScope.(arguments: FlexArguments) -> Map<String, *>?): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun setAction(name: String, action: suspend CoroutineScope.(action: FlexAction, arguments: FlexArguments) -> Unit): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        actions[name] = action
        return this
    }


    fun voidInterfaceForJava(name: String, invoke: InvokeFlexVoid): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun stringInterfaceForJava(name: String, invoke: InvokeFlex<String>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun intInterfaceForJava(name: String, invoke: InvokeFlex<Int>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun charInterfaceForJava(name: String, invoke: InvokeFlex<Char>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun longInterfaceForJava(name: String, invoke: InvokeFlex<Long>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun doubleInterfaceForJava(name: String, invoke: InvokeFlex<Double>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun floatInterfaceForJava(name: String, invoke: InvokeFlex<Float>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun boolInterfaceForJava(name: String, invoke: InvokeFlex<Boolean>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun arrayInterfaceForJava(name: String, invoke: InvokeFlex<Array<*>>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun listInterfaceForJava(name: String, invoke: InvokeFlex<Iterable<*>>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun mapInterfaceForJava(name: String, invoke: InvokeFlex<Map<String, *>>): FlexInterfaces {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun setActionForJava(name: String, invoke: InvokeAction): FlexInterfaces {
        return setAction(name) { ac, args -> invoke.invoke(ac, args) }
    }

}