package app.dvkyun.flexhybridand

import org.json.JSONArray

open class FlexInterfaces {

    internal val interfaces: HashMap<String,(JSONArray?) -> Any?> = HashMap()
    internal val actions: HashMap<String,(action: FlexAction?, arguments: JSONArray?) -> Unit> = HashMap()

    @Target(AnnotationTarget.FUNCTION)
    annotation class SetInterface

    @Target()

    @Target(AnnotationTarget.FUNCTION)
    annotation class setAction



    fun setInterface(name: String, lambda: (JSONArray?) -> Any?) {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR8)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR9)
        }
        interfaces[name] = lambda
    }

    fun setAction(name: String, action: (action: FlexAction?, arguments: JSONArray?) -> Unit) {
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR8)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR9)
        }
        actions[name] = action
    }

}