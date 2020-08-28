package app.dvkyun.flexhybridand

import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import java.lang.Exception


data class Run<T>(
    val block: (argument: JSONArray) -> T
)

data class Scope<T>(
    val block: suspend CoroutineScope.(argument: JSONArray) -> T
)

@Suppress("UNCHECKED_CAST")
class Data {
    private var data: Any? = null
    
    fun set(data: String): Data {
        this.data = data
        return this
    }

    fun set(data: Int): Data {
        this.data = data
        return this
    }

    fun set(data: Long): Data {
        this.data = data
        return this
    }

    fun set(data: Float): Data {
        this.data = data
        return this
    }

    fun set(data: Double): Data {
        this.data = data
        return this
    }

    fun set(data: Char): Data {
        this.data = data
        return this
    }

    fun set(data: Boolean): Data {
        this.data = data
        return this
    }

    fun set(data: Array<Data>): Data {
        this.data = data
        return this
    }

    fun set(data: Iterable<Data>): Data {
        this.data = data
        return this
    }

    fun set(data: Map<String, Data>): Data {
        this.data = data
        return this
    }

    fun set(data: Unit): Data {
        this.data = data
        return this
    }

    fun set(data: Void): Data {
        this.data = data
        return this
    }

    fun set(data: FlexReject): Data {
        this.data = data
        return this
    }

    fun set(): Data {
        this.data = null
        return this
    }
    
    fun asString(): String {
        return data as String
    }

    fun asInt(): Int {
        return data as Int
    }

    fun asLong(): Long {
        return data as Long
    }

    fun asFloat(): Float {
        return data as Float
    }

    fun asDouble(): Double {
        return data as Double
    }

    fun getChar(): Char {
        return data as Char
    }

    fun asBoolean(): Boolean {
        return data as Boolean
    }

    fun asArray(): Array<Data> {
        return data as Array<Data>
    }

    fun asList(): Iterable<Data> {
        return data as Iterable<Data>
    }

    fun asMap(): Map<String, Data> {
        return data as Map<String, Data>
    }

    fun asLambda(): Unit {
        return data as Unit
    }

    fun asVoid(): Void {
        return data as Void
    }

    fun asErr(): FlexReject {
        return data as FlexReject
    }

    inline fun <reified T> to() : T {
        return when(T::class) {
            String::class -> asString() as T
            Int::class -> asInt() as T
            else -> throw Exception()
        }
    }

}


