package app.dvkyun.flexhybridand.demo

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.*
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    
    private var flexWebView: FlexWebView? = null

    private val test4 = FlexLambda.action { action, _: FlexArguments ->
        val arrayData = ArrayList<Int>()
        arrayData.add(1)
        arrayData.add(22)
        arrayData.add(333)
        val objectData = HashMap<String, Any?>()
        objectData["o1"] = null
        objectData["o2"] = 999.9999
        objectData["o3"] = "dataO3"
        val data = ActionDataTest(
            1, "test test", 1.0000000001, false, arrayData, objectData
        )
        Log.i("console", "Send to web --- $data")
        action.promiseReturn(data)
    }

    private val test5 = FlexLambda.void { _: FlexArguments ->
        flexWebView?.evalFlexFunc("webtest", "hi! \$flex!") { response: FlexData ->
            val arr = response.asArray() ?: emptyArray()
            Log.i(
                "console",
                "Receive from web --- " + arr[0].asString() + arr[1]
                    .asInt() + arr[2].asMap().toString()
            )
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        flexWebView = findViewById(R.id.flex_web_view)
        flexWebView?.webViewClient = object : FlexWebViewClient() {
            override fun notAllowedUrlLoad(
                view: WebView,
                request: WebResourceRequest?,
                url: String?
            ) {
                super.notAllowedUrlLoad(view, request, url)
                flexWebView?.evalFlexFunc("notAllowed", url ?: "")
            }
        }
        flexWebView?.evalFlexFunc("webtest", "webtest")

        flexWebView?.addFlexEventListener { _, type, url, funcName, msg ->
            Log.i("listener", "type: ${type.name} url: $url funcName: $funcName msg: $msg")
        }
        flexWebView?.setInterfaceTimeout(3000)
        flexWebView?.setInterfaceThreadCount(63)
//        flexWebView?.setCoroutineContext(Dispatchers.getIO())
        flexWebView?.settings?.textZoom = 250

        flexWebView?.addFlexInterface(FlexInterfaceExample())
        flexWebView?.addFlexInterface(FlexInterfaceExample2(this))
        flexWebView?.setAction("test4", action = test4)
        flexWebView?.setInterface("test5", lambda = test5)

//        flexWebView?.setBaseUrl("file:///android_asset")
//        flexWebView?.addAllowUrl(".google.com", true)
//        flexWebView?.setAllowFileAccessAndUrlAccessInFile(true)
//        flexWebView?.loadUrl("file:///android_asset/html/test.html")
        flexWebView?.baseUrl = "appassets.androidplatform.net"
        flexWebView?.setAssetsLoaderUse(true, "/assets/")
        flexWebView?.addAllowUrl(".google.com", true)
        flexWebView?.addAllowUrl(".facebook.com", false)
        flexWebView?.loadUrl("https://appassets.androidplatform.net/assets/html/test.html")
    }
}