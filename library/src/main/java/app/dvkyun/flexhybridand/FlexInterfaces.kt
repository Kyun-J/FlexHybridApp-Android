package app.dvkyun.flexhybridand

import org.json.JSONArray

open class FlexInterfaces {

    internal val interfaces: HashMap<String,(JSONArray?) -> Any?> = HashMap()
    internal val actions: HashMap<String,(action: FlexAction?, arguments: JSONArray?) -> Unit> = HashMap()

    fun setInterface(name: String, lambda: (JSONArray?) -> Any?): FlexInterfaces {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        interfaces[name] = lambda
        return this
    }

    fun setAction(name: String, action: (action: FlexAction?, arguments: JSONArray?) -> Unit): FlexInterfaces {
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