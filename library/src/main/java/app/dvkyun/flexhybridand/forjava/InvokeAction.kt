package app.dvkyun.flexhybridand.forjava

import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexData

interface InvokeAction {
    fun invoke(action: FlexAction, arguments: Array<FlexData>)
}