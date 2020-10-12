package app.dvkyun.flexhybridand.demo

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.*
import app.dvkyun.flexhybridand.demo.KtObject.nowAppContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class FlexInterfaceExample2: FlexInterfaces() {

    init {
        voidInterface("test6")
        {
            (nowAppContext as Activity).runOnUiThread {
                AlertDialog.Builder(nowAppContext as Activity)
                    .setTitle("DialogTest")
                    .setMessage("AlertDialogSuccess!!")
                    .create()
                    .show()
            }
        }.setAction("test7")
        { action, _ ->
            val res = async {
                delay(10)
                "test success"
            }.await()
            action.onFinished = {
                Log.i("console", "test7 finished!")
            }
            action.promiseReturn(res)
        }
    }

    @FlexFuncInterface
    suspend fun test8(arguments: Array<FlexData>) {
        throw FlexException("Exception test!")
    }

    @FlexActionInterface
    fun test9(action: FlexAction, arguments: Array<FlexData>) {
        action.reject("action reject test")
    }

}