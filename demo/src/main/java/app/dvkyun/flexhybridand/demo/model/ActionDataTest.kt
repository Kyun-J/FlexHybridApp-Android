package app.dvkyun.flexhybridand.demo.model

import app.dvkyun.flexhybridand.FlexType

data class ActionDataTest(
    val intData: Int,
    val StringData: String,
    val doubleData: Double,
    val boolData: Boolean,
    val arrayData: ArrayList<Int>,
    val objectData: HashMap<String, Any?>
) : FlexType
