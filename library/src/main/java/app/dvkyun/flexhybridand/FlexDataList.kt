package app.dvkyun.flexhybridand

class FlexDataList internal constructor(private val array: Array<FlexData>) {

    operator fun get(index: Int): FlexData = array[index]

    operator fun set(index: Int, value: FlexData): Unit = array.set(index, value)

    val size: Int = array.size

    operator fun iterator(): Iterator<FlexData> = array.iterator()

    fun toArray(): Array<FlexData> = array

}