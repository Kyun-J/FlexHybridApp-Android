package app.dvkyun.flexhybridand

open class FlexInterfaces {

    internal val interfaces: HashMap<String, suspend (Array<FlexData>) -> Any?> = HashMap()
    internal val actions: HashMap<String, suspend (action: FlexAction?, arguments: Array<FlexData>) -> Unit> = HashMap()

    private fun setInterface(name: String, lambda: suspend (Array<FlexData>) -> Any?): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        interfaces[name] = lambda
        return this
    }

    fun voidInterface(name: String, lambda: suspend (Array<FlexData>) -> Unit): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun stringInterface(name: String, lambda: suspend (Array<FlexData>) -> String): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun intInterface(name: String, lambda: suspend (Array<FlexData>) -> Int): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun charInterface(name: String, lambda: suspend (Array<FlexData>) -> Char): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun longInterface(name: String, lambda: suspend (Array<FlexData>) -> Long): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun doubleInterface(name: String, lambda: suspend (Array<FlexData>) -> Double): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun floatInterface(name: String, lambda: suspend (Array<FlexData>) -> Float): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun boolInterface(name: String, lambda: suspend (Array<FlexData>) -> Boolean): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun arrayInterface(name: String, lambda: suspend (Array<FlexData>) -> Array<*>): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun mapInterface(name: String, lambda: suspend (Array<FlexData>) -> Map<String, *>): FlexInterfaces {
        return setInterface(name, lambda)
    }

    fun setAction(name: String, action: suspend (action: FlexAction?, arguments: Array<FlexData>) -> Unit): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        actions[name] = action
        return this
    }

}