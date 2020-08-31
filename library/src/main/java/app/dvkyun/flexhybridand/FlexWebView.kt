package app.dvkyun.flexhybridand

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.io.Reader
import java.lang.Exception
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

open class FlexWebView: WebView {

    companion object {
        private const val UNIQUE = "flexdefine"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val activity: Activity? = FlexUtil.getActivity(context)
    private val interfaces : HashMap<String, (arguments: Array<FlexData>) -> Any?> = HashMap()
    private val actions: HashMap<String, (action: FlexAction, arguments: Array<FlexData>) -> Unit> = HashMap()
    private val options: JSONObject = JSONObject()
    private val dependencies: ArrayList<String> = ArrayList()
    private val returnFromWeb: HashMap<Int,(FlexData) -> Unit> = HashMap()
    private val internalInterface = arrayOf("flexreturn", "flexload")
    private var tCount = Runtime.getRuntime().availableProcessors()
    val scope by lazy {
        CoroutineScope(Executors.newFixedThreadPool(tCount).asCoroutineDispatcher())
    }
    var isFlexLoad: Boolean = false
        private set
    private val beforeFlexLoadEvalList =  ArrayList<BeforeFlexEval>()

    private var isAfterFirstLoad = false
    private var flexJsString: String

    var baseUrl: String? = null
        set(value) {
            if(field != null) {
                throw FlexException(FlexException.ERROR5)
            }
            if(value == null){
                field = value
                return
            }
            if(!value.startsWith("http://") && !value.startsWith("http://") && !value.startsWith("file://")) {
                throw FlexException(FlexException.ERROR13)
            }
            field = value
        }

    init {
        if(activity == null) throw FlexException(FlexException.ERROR1)
        flexJsString = FlexUtil.fileToString(context.assets.open("FlexHybridAnd.js"))
        webChromeClient = FlexWebChromeClient(activity)
        webViewClient = FlexWebViewClient()
        super.addJavascriptInterface(InternalInterface(), UNIQUE)
        initialize()
    }

    /*
    * Initial setting
    * */
    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    fun initialize() {
        settings.javaScriptEnabled = true
        settings.displayZoomControls = false
        settings.builtInZoomControls = false
        settings.setSupportZoom(false)
        settings.textZoom = 100
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.mediaPlaybackRequiresUserGesture = false
        settings.enableSmoothTransition()
        settings.javaScriptCanOpenWindowsAutomatically = true
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, true)
        }
    }

    private fun setInterface(name: String, lambda: (Array<FlexData>) -> Any?): FlexWebView {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        interfaces[name] = lambda
        return this
    }

    fun voidInterface(name: String, lambda: (Array<FlexData>) -> Unit): FlexWebView {
        return setInterface(name, lambda)
    }

    fun stringInterface(name: String, lambda: (Array<FlexData>) -> String): FlexWebView {
        return setInterface(name, lambda)
    }

    fun intInterface(name: String, lambda: (Array<FlexData>) -> Int): FlexWebView {
        return setInterface(name, lambda)
    }

    fun charInterface(name: String, lambda: (Array<FlexData>) -> Char): FlexWebView {
        return setInterface(name, lambda)
    }

    fun longInterface(name: String, lambda: (Array<FlexData>) -> Long): FlexWebView {
        return setInterface(name, lambda)
    }

    fun doubleInterface(name: String, lambda: (Array<FlexData>) -> Double): FlexWebView {
        return setInterface(name, lambda)
    }

    fun floatInterface(name: String, lambda: (Array<FlexData>) -> Float): FlexWebView {
        return setInterface(name, lambda)
    }

    fun boolInterface(name: String, lambda: (Array<FlexData>) -> Boolean): FlexWebView {
        return setInterface(name, lambda)
    }

    fun arrayInterface(name: String, lambda: (Array<FlexData>) -> Array<*>): FlexWebView {
        return setInterface(name, lambda)
    }

    fun mapInterface(name: String, lambda: (Array<FlexData>) -> Map<String, *>): FlexWebView {
        return setInterface(name, lambda)
    }


    fun setAction(name: String, action: (action: FlexAction, arguments: Array<FlexData>) -> Unit): FlexWebView {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        actions[name] = action
        return this
    }

    fun addFlexInterface(flexInterfaces: Any) {
        flexInterfaces::class.members.forEach {
            if(it.findAnnotation<FlexFuncInterface>() != null) {
                if(it.visibility != KVisibility.PUBLIC) {
                    throw FlexException(FlexException.ERROR12)
                }
                val inputClass = it.valueParameters[0].type
                val checkClass = Array::class.createType(List(1) { KTypeProjection.invariant(FlexData::class.starProjectedType) })
                if(it.valueParameters.size != 1 || !(inputClass.classifier == checkClass.classifier && inputClass.arguments[0].type?.classifier == checkClass.arguments[0].type?.classifier)) {
                    throw FlexException(FlexException.ERROR10)
                }
                setInterface(it.name) { arguments ->
                    it.call(flexInterfaces, arguments)
                }
            } else if(it.findAnnotation<FlexActionInterface>() != null) {
                if(it.visibility != KVisibility.PUBLIC) {
                    throw FlexException(FlexException.ERROR12)
                }
                val inputClass = it.valueParameters[1].type
                val checkClass = Array::class.createType(List(1) { KTypeProjection.invariant(FlexData::class.starProjectedType) })
                if(it.valueParameters.size != 2 || it.valueParameters[0].type.classifier != FlexAction::class.starProjectedType.classifier || !(inputClass.classifier == checkClass.classifier && inputClass.arguments[0].type?.classifier == checkClass.arguments[0].type?.classifier)) {
                    throw FlexException(FlexException.ERROR11)
                }
                setAction(it.name) { action, arguments ->
                    it.call(flexInterfaces, action, arguments)
                }
            }
        }
        if(flexInterfaces is FlexInterfaces) {
            flexInterfaces.interfaces.keys.forEach {
                setInterface(it, flexInterfaces.interfaces[it]!!)
            }
            flexInterfaces.actions.keys.forEach {
                setAction(it, flexInterfaces.actions[it]!!)
            }
        }
    }

    fun setInterfaceTimeout(timeout: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        options.put("timeout", timeout)
    }

    fun setFlexOnLoadWait(timeout: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        options.put("flexLoadWait", timeout)
    }

    fun setInterfaceThreadCount(count: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        tCount = count
    }

    fun setDependency(inputStream: InputStream) {
        dependencies.add(FlexUtil.fileToString(inputStream))
    }

    fun setDependency(reader: Reader) {
        dependencies.add(FlexUtil.fileToString(reader))
    }

    fun setDependency(js: String) {
        dependencies.add(js)
    }

    fun evalFlexFunc(funcName: String) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName))
        } else {
            FlexUtil.evaluateJavaScript(this,"\$flex.web.$funcName(); void 0;")
        }
    }

    fun evalFlexFunc(funcName: String, response: (FlexData) -> Unit) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, response))
        } else {
            val tID = Random.nextInt(10000)
            returnFromWeb[tID] = response
            FlexUtil.evaluateJavaScript(this,"!async function(){try{const e=await \$flex.web.$funcName();\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}catch(e){\$flex.flexreturn({TID:$tID,Value:e,Error:!0})}}();void 0;")
        }
    }

    fun evalFlexFunc(funcName: String, sendData: Any) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, sendData))
        } else {
            FlexUtil.evaluateJavaScript(this, "\$flex.web.$funcName(${FlexUtil.convertInput(sendData)}); void 0;")
        }
    }

    fun evalFlexFunc(funcName: String, sendData: Any, response: (FlexData) -> Unit) {
        if (!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, sendData, response))
        } else {
            val tID = Random.nextInt(10000)
            returnFromWeb[tID] = response
            FlexUtil.evaluateJavaScript(this, "!async function(){try{const e=await \$flex.web.$funcName(${FlexUtil.convertInput(sendData)});\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}catch(e){\$flex.flexreturn({TID:$tID,Value:e,Error:!0})}}();void 0;")
        }
    }

    override fun getWebChromeClient(): FlexWebChromeClient {
        return super.getWebChromeClient() as FlexWebChromeClient
    }

    override fun setWebChromeClient(client: WebChromeClient?) {
        if(client !is FlexWebChromeClient) throw FlexException(FlexException.ERROR2)
        super.setWebChromeClient(client)
    }

    override fun getWebViewClient(): FlexWebViewClient {
        return super.getWebViewClient() as FlexWebViewClient
    }

    override fun setWebViewClient(client: WebViewClient) {
        if(client !is FlexWebViewClient) throw FlexException(FlexException.ERROR2)
        super.setWebViewClient(client)
    }

    @SuppressLint("JavascriptInterface")
    override fun addJavascriptInterface(`object`: Any, name: String) {
        if(name.contains("flex")) {
            throw FlexException(FlexException.ERROR8)
        }
        super.addJavascriptInterface(`object`, name)
    }

    override fun removeJavascriptInterface(name: String) {
        if(name == UNIQUE) {
            throw FlexException(FlexException.ERROR14)
        }
        super.removeJavascriptInterface(name)
    }

    internal fun flexInitInPage() {
        if(!isAfterFirstLoad) {
            val keys = StringBuilder()
            keys.append("[\"")
            keys.append(internalInterface.joinToString(separator = "\",\""))
            if(interfaces.size > 0) {
                keys.append("\",\"")
                keys.append(interfaces.keys.joinToString(separator = "\",\""))
            }
            if(actions.size > 0) {
                keys.append("\",\"")
                keys.append(actions.keys.joinToString(separator = "\",\""))
            }
            keys.append("\"]")
            flexJsString = flexJsString.replaceFirst("keysfromAnd",keys.toString())
            flexJsString = flexJsString.replaceFirst("optionsfromAnd", FlexUtil.convertInput(options))
            val device = JSONObject()
            device.put("os","Android")
            device.put("version", Build.VERSION.SDK_INT)
            device.put("model", Build.MODEL)
            flexJsString = flexJsString.replaceFirst("deviceinfoFromAnd", FlexUtil.convertInput(device))
        }
        isAfterFirstLoad = true
        FlexUtil.evaluateJavaScript(this, flexJsString)
        dependencies.forEach {
            FlexUtil.evaluateJavaScript(this, it)
        }
    }

    inner class InternalInterface {
        @JavascriptInterface
        fun flexInterface(input: String) {
            scope.launch {
                try {
                    val data = JSONObject(input)
                    val intName: String = data.getString("intName")
                    val fName: String = data.getString("funName")
                    val args: JSONArray = data.getJSONArray("arguments")
                    try {
                        if (interfaces[intName] != null) {
                            val value = interfaces[intName]?.invoke(FlexUtil.jsonArrayToFlexData(args))
                            if (value is BrowserException) {
                                val reason =
                                    if (value.reason == null) null else "\"${value.reason}\""
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.${fName}(false, ${reason})"
                                )
                            } else if (value == null || value == Unit || value is Void) {
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.${fName}(true)"
                                )
                            } else {
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.$fName(true, null, ${FlexUtil.convertInput(value)})"
                                )
                            }
                        } else if (actions[intName] != null) {
                            actions[intName]?.invoke(FlexAction(fName, this@FlexWebView), FlexUtil.jsonArrayToFlexData(args))
                        } else {
                            when (internalInterface.indexOf(intName)) {
                                0 -> { // flexreturn
                                    val iData = args.getJSONObject(0)
                                    val tID = iData.getInt("TID")
                                    val value = iData.opt("Value")
                                    val error = iData.getBoolean("Error")
                                    if (error) {
                                        val errMsg: String = value?.let { value.toString() } ?: "null"
                                        returnFromWeb[tID]?.invoke(FlexUtil.anyToFlexData(BrowserException(errMsg)))
                                    } else {
                                        returnFromWeb[tID]?.invoke(FlexUtil.anyToFlexData(value))
                                    }
                                    FlexUtil.evaluateJavaScript(
                                        this@FlexWebView,
                                        "\$flex.flex.${fName}(true)"
                                    )
                                }
                                1 -> { // flexload
                                    isFlexLoad = true
                                    beforeFlexLoadEvalList.forEach {
                                        if(it.sendData != null && it.response != null) {
                                            evalFlexFunc(it.name, it.sendData, it.response)
                                        } else if(it.sendData != null && it.response == null) {
                                            evalFlexFunc(it.name, it.sendData)
                                        } else if(it.sendData == null && it.response != null) {
                                            evalFlexFunc(it.name, it.response)
                                        } else {
                                            evalFlexFunc(it.name)
                                        }
                                    }
                                    beforeFlexLoadEvalList.clear()
                                    FlexUtil.evaluateJavaScript(
                                        this@FlexWebView,
                                        "\$flex.flex.${fName}(true)"
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        FlexUtil.ERR(e)
                        FlexUtil.evaluateJavaScript(
                            this@FlexWebView,
                            "\$flex.flex.${fName}(false, \"${e.cause?.message}\")"
                        )
                        e.printStackTrace()
                    }
                } catch (e: JSONException) {
                    FlexUtil.ERR(e)
                    e.printStackTrace()
                }
            }
        }
    }

    inner class BeforeFlexEval {
        val name:String
        val sendData: Any?
        val response: ((FlexData) -> Unit)?

        constructor(name: String) {
            this.name = name
            sendData = null
            response = null
        }
        constructor(name: String, sendData:Any) {
            this.name = name
            this.sendData = sendData
            response = null
        }
        constructor(name: String, response:(FlexData) -> Unit) {
            this.name = name
            this.response = response
            sendData = null
        }
        constructor(name: String, sendData:Any, response: (FlexData) -> Unit) {
            this.name = name
            this.sendData = sendData
            this.response = response
        }
    }

}
