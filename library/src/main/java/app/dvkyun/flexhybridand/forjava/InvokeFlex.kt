package app.dvkyun.flexhybridand.forjava

import app.dvkyun.flexhybridand.FlexArguments

interface InvokeFlex<T> {
    fun invoke(arguments: FlexArguments): T
}