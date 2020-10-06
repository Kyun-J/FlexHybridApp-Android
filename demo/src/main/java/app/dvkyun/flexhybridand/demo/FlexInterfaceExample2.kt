package app.dvkyun.flexhybridand.demo

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.*
import app.dvkyun.flexhybridand.demo.KtObject.nowAppContext

class FlexInterfaceExample2 {

    @FlexFuncInterface
    suspend fun test6(arguments: Array<FlexData>) {
        (nowAppContext as Activity).runOnUiThread {
            AlertDialog.Builder(nowAppContext as Activity)
                .setTitle("DialogTest")
                .setMessage("AlertDialogSuccess!!")
                .create()
                .show()
        }
    }

    @FlexActionInterface
    suspend fun test7(action: FlexAction, arguments: Array<FlexData>) {
        Log.i("console", "Annotation Action Interface test")
        action.promiseReturn("test success")
    }

    @FlexFuncInterface
    fun test8(arguments: Array<FlexData>) {
        throw FlexException("Exception test!")
    }

    @FlexActionInterface
    fun test9(action: FlexAction, arguments: Array<FlexData>) {
        action.reject("action reject test")
    }

}