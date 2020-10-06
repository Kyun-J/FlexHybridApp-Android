package app.dvkyun.flexhybridand.forjava

import app.dvkyun.flexhybridand.FlexData

interface InvokeFlex<T> {
    fun invoke(arguments: Array<FlexData>): T
}