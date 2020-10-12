package app.dvkyun.flexhybridand

class FlexDataList internal constructor(private val array: Array<FlexData>) {

    operator fun get(index: Int): FlexData = array[index]

    val size: Int = array.size

    operator fun iterator(): Iterator<FlexData> = array.iterator()

    fun toArray(): Array<FlexData> = array

}