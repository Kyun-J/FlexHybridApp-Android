package app.dvkyun.flexhybridand

import java.util.*

class FlexListener(internal val listener: (type: FlexEvent, url: String, funcName: String, msg: String) -> Unit) {
    internal val id: UUID = UUID.randomUUID()
}