package app.dvkyun.flexhybridand.demo

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.*
import app.dvkyun.flexhybridand.demo.model.DataTest1
import app.dvkyun.flexhybridand.demo.model.DataTest2
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class FlexInterfaceClassExample(private val activity: Activity) : FlexInterfaces() {

    init {
        setInterface("test6")
        {
            activity.runOnUiThread {
                AlertDialog.Builder(activity)
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
        }.typeAction("test10")
        { action, args: DataTest1 ->
            Log.i("test10", args.data[0].test + " model test")
            action.promiseReturn(DataTest2(args.data, args.testLong + 1))
        }
    }

    @FlexFuncInterface
    suspend fun test8(arguments: FlexArguments) {
        Log.i("console", arguments[0]?.asString().toString())
        throw FlexException("Exception test!")
    }

    @FlexActionInterface
    fun test9(action: FlexAction, arguments: FlexArguments) {
        action.reject("action reject test")
    }

}