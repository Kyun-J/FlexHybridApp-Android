package app.dvkyun.flexhybridand

@Suppress("UNCHECKED_CAST")
class FlexData {
    internal val data: Any
    var type = Type.NULL
        private set

    enum class Type {
        STRING,
        INT,
        LONG,
        DOUBLE,
        BOOLEAN,
        ARRAY,
        MAP,
        NULL,
        ERR
    }

    constructor(data : String) {
        this.data = data
        type = Type.STRING
    }

    constructor(data : Int) {
        this.data = data
        type = Type.INT
    }

    constructor(data : Long) {
        this.data = data
        type = Type.LONG
    }

    constructor(data : Double) {
        this.data = data
        type = Type.DOUBLE
    }

    constructor(data : Boolean) {
        this.data = data
        type = Type.BOOLEAN
    }

    constructor(data : Array<FlexData?>) {
        this.data = data
        type = Type.ARRAY
    }

    constructor(data : Map<String, FlexData?>) {
        this.data = data
        type = Type.MAP
    }

    constructor(data: BrowserException) {
        this.data = data
        type = Type.ERR
    }
    
    fun asString(): String {
        if(type != Type.STRING) throw FlexException(FlexException.ERROR15)
        return data.toString()
    }

    fun asInt(): Int {
        if(type != Type.INT) throw FlexException(FlexException.ERROR15)
        return data as Int
    }

    fun asLong(): Long {
        if(type != Type.LONG) throw FlexException(FlexException.ERROR15)
        return data as Long
    }

    fun asDouble(): Double {
        if(type != Type.DOUBLE) throw FlexException(FlexException.ERROR15)
        return data as Double
    }

    fun asBoolean(): Boolean {
        if(type != Type.BOOLEAN) throw FlexException(FlexException.ERROR15)
        return data as Boolean
    }

    fun asArray(): Array<FlexData> {
        if(type != Type.ARRAY) throw FlexException(FlexException.ERROR15)
        return data as Array<FlexData>
    }

    fun asMap(): Map<String, FlexData> {
        if(type != Type.MAP) throw FlexException(FlexException.ERROR15)
        return data as Map<String, FlexData>
    }

    fun asErr(): BrowserException {
        if(type != Type.ERR) throw FlexException(FlexException.ERROR15)
        return data as BrowserException
    }

    inline fun <reified T> to() : T {
        if(T::class == String::class && type == Type.STRING){
            return asString() as T
        } else if(T::class == Int::class && type == Type.INT){
            return asInt() as T
        } else if(T::class == Long::class && type == Type.LONG){
            return asLong() as T
        } else if(T::class == Double::class && type == Type.DOUBLE){
            return asDouble() as T
        } else if(T::class == Boolean::class && type == Type.BOOLEAN){
            return asBoolean() as T
        } else if(T::class == Array::class && type == Type.ARRAY){
            return asArray() as T
        } else if(T::class == Map::class && type == Type.MAP){
            return asMap() as T
        } else if(T::class == BrowserException::class && type == Type.ERR){
            return asErr() as T
        } else {
            throw FlexException(FlexException.ERROR15)
        }
    }

}


