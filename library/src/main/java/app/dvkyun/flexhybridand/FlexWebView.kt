package app.dvkyun.flexhybridand

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.annotation.RequiresApi
import app.dvkyun.flexhybridand.forjava.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.io.Reader
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

open class FlexWebView: WebView {

    companion object {
        private const val VERSION = "0.7.1"
        private val UNIQUE = UUID.randomUUID().toString()
        private const val FLEX = "flex"

        /*
        * options
        * */
        private const val OPT_TIMEOUT = "timeout"
        private const val OPT_LOAD_WAIT = "flexLoadWait"

        /*
        * internal interface
        * */
        private const val INT_RETURN = "flexreturn"
        private const val INT_LOAD = "flexload"
        /*
        * event interface
        * */
        private const val EVT_SUC = "flexSuccess"
        private const val EVT_EXC = "flexException"
        private const val EVT_TIMEOUT = "flexTimeout"
        private const val EVT_INIT = "flexInit"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var activity: Activity? = null
        internal set
        get() = FlexUtil.getActivity(context)
    private val interfaces : HashMap<String, suspend CoroutineScope.(arguments: Array<FlexData>) -> Any?> = HashMap()
    private val actions: HashMap<String, suspend CoroutineScope.(action: FlexAction, arguments: Array<FlexData>) -> Unit> = HashMap()
    private val options: JSONObject = JSONObject()
    private val dependencies: ArrayList<String> = ArrayList()
    private val returnFromWeb: HashMap<Int, suspend CoroutineScope.(FlexData) -> Unit> = HashMap()
    private val internalInterface = arrayOf(INT_RETURN, INT_LOAD, EVT_SUC, EVT_EXC, EVT_TIMEOUT, EVT_INIT)
    private var tCount = Runtime.getRuntime().availableProcessors()
    private var cContext: CoroutineContext? = null
    val scope by lazy {
        cContext?.let {
            CoroutineScope(it)
        } ?: CoroutineScope(Executors.newFixedThreadPool(tCount).asCoroutineDispatcher())
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
            field = value
            baseUri = Uri.parse(value)
        }
    var baseUri: Uri? = null
    internal val allowUrlList: ArrayList<Pair<String, Boolean>> = ArrayList()

    fun addAllowUrl(url: String, canUseFlex: Boolean = false) {
//        if(baseUrl == null) throw FlexException(FlexException.ERROR13)
        allowUrlList.add(Pair(url, canUseFlex))
    }

    private var flexEventList : ArrayList<Pair<FlexEvent, FlexListener>> = ArrayList()
    private var flexEventListJava : ArrayList<Pair<FlexEvent, FlexListenerForJava>> = ArrayList()

    fun addFlexEventListener(type: FlexEvent, listener: FlexListener) {
        flexEventList.add(Pair(type,listener))
    }

    fun addFlexEventListener(type: FlexEvent, listener: (view: FlexWebView, type: FlexEvent, url: String, funcName: String, msg: String?) -> Unit) {
        flexEventList.add(Pair(type, FlexListener(listener)))
    }

    fun addFlexEventListener(listener: FlexListener) {
        flexEventList.add(Pair(FlexEvent.SUCCESS, listener))
        flexEventList.add(Pair(FlexEvent.EXCEPTION, listener))
        flexEventList.add(Pair(FlexEvent.TIMEOUT, listener))
        flexEventList.add(Pair(FlexEvent.INIT, listener))
    }

    fun addFlexEventListener(listener: (view: FlexWebView, type: FlexEvent, url: String, funcName: String, msg: String?) -> Unit) {
        flexEventList.add(Pair(FlexEvent.SUCCESS, FlexListener(listener)))
        flexEventList.add(Pair(FlexEvent.EXCEPTION, FlexListener(listener)))
        flexEventList.add(Pair(FlexEvent.TIMEOUT, FlexListener(listener)))
        flexEventList.add(Pair(FlexEvent.INIT, FlexListener(listener)))
    }

    fun addFlexEventListenerForJava(type: FlexEvent, listener: FlexListenerForJava) {
        flexEventListJava.add(Pair(type, listener))
    }
    fun addFlexEventListenerForJava(listener: FlexListenerForJava) {
        flexEventListJava.add(Pair(FlexEvent.SUCCESS, listener))
        flexEventListJava.add(Pair(FlexEvent.EXCEPTION, listener))
        flexEventListJava.add(Pair(FlexEvent.TIMEOUT, listener))
        flexEventListJava.add(Pair(FlexEvent.INIT, listener))
    }

    fun removeFlexEventListener(listener: FlexListener) {
        flexEventList = flexEventList.filter { _item ->
            _item.second.id != listener.id
        } as ArrayList<Pair<FlexEvent, FlexListener>>
    }

    fun removeAllFlexEventListener() {
        flexEventList = ArrayList()
        flexEventListJava = ArrayList()
    }

    init {
        val atv = activity ?: throw FlexException(FlexException.ERROR1)
        flexJsString = FlexUtil.fileToString(context.assets.open("FlexHybridAnd.js"))
        webChromeClient = FlexWebChromeClient(atv)
        webViewClient = FlexWebViewClient()
        super.addJavascriptInterface(InternalInterface(), UNIQUE)
        initialize()
    }

    /*
    * Initial setting
    * */
    @SuppressLint("SetJavaScriptEnabled")
    fun initialize() {
        if(BuildConfig.DEBUG) setWebContentsDebuggingEnabled(true)
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

    private fun setInterface(name: String, lambda: suspend CoroutineScope.(Array<FlexData>) -> Any?): FlexWebView {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains(FLEX)) {
            throw FlexException(FlexException.ERROR8)
        }
        interfaces[name] = lambda
        return this
    }

    fun voidInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Unit): FlexWebView {
        return setInterface(name, lambda)
    }

    fun stringInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> String?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun intInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Int?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun charInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Char?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun longInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Long?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun doubleInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Double?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun floatInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Float?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun boolInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Boolean?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun arrayInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Array<*>?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun listInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Iterable<*>?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun mapInterface(name: String, lambda: suspend CoroutineScope.(arguments: Array<FlexData>) -> Map<String, *>?): FlexWebView {
        return setInterface(name, lambda)
    }

    fun setAction(name: String, action: suspend CoroutineScope.(action: FlexAction, arguments: Array<FlexData>) -> Unit): FlexWebView {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        if(interfaces[name] != null || actions[name] != null) {
            throw FlexException(FlexException.ERROR7)
        }
        if(name.contains(FLEX)) {
            throw FlexException(FlexException.ERROR8)
        }
        actions[name] = action
        return this
    }

    fun voidInterfaceForJava(name: String, invoke: InvokeFlexVoid): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun stringInterfaceForJava(name: String, invoke: InvokeFlex<String>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun intInterfaceForJava(name: String, invoke: InvokeFlex<Int>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun charInterfaceForJava(name: String, invoke: InvokeFlex<Char>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun longInterfaceForJava(name: String, invoke: InvokeFlex<Long>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun doubleInterfaceForJava(name: String, invoke: InvokeFlex<Double>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun floatInterfaceForJava(name: String, invoke: InvokeFlex<Float>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun boolInterfaceForJava(name: String, invoke: InvokeFlex<Boolean>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun arrayInterfaceForJava(name: String, invoke: InvokeFlex<Array<*>>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun listInterfaceForJava(name: String, invoke: InvokeFlex<Iterable<*>>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun mapInterfaceForJava(name: String, invoke: InvokeFlex<Map<String, *>>): FlexWebView {
        return setInterface(name) { args -> invoke.invoke(args) }
    }

    fun setActionForJava(name: String, invoke: InvokeAction): FlexWebView {
        return setAction(name) { ac, args -> invoke.invoke(ac, args) }
    }

    fun addFlexInterface(flexInterfaces: Any) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        flexInterfaces::class.members.forEach {
            if(it.hasAnnotation<FlexFuncInterface>()) {
                if(it.visibility != KVisibility.PUBLIC) {
                    throw FlexException(FlexException.ERROR12)
                }
                val inputClass = it.valueParameters[0].type
                val checkClass = Array::class.createType(List(1) { KTypeProjection.invariant(FlexData::class.starProjectedType) })
                if(it.valueParameters.size != 1 || !(inputClass.classifier == checkClass.classifier && inputClass.arguments[0].type?.classifier == checkClass.arguments[0].type?.classifier)) {
                    throw FlexException(FlexException.ERROR10)
                }
                setInterface(it.name) { arguments ->
                    if(it.isSuspend) it.callSuspend(flexInterfaces, arguments)
                    else it.call(flexInterfaces, arguments)
                }
            } else if(it.hasAnnotation<FlexActionInterface>()) {
                if(it.visibility != KVisibility.PUBLIC) {
                    throw FlexException(FlexException.ERROR12)
                }
                val inputClass = it.valueParameters[1].type
                val checkClass = Array::class.createType(List(1) { KTypeProjection.invariant(FlexData::class.starProjectedType) })
                if(it.valueParameters.size != 2 || it.valueParameters[0].type.classifier != FlexAction::class.starProjectedType.classifier || !(inputClass.classifier == checkClass.classifier && inputClass.arguments[0].type?.classifier == checkClass.arguments[0].type?.classifier)) {
                    throw FlexException(FlexException.ERROR11)
                }
                setAction(it.name) { action, arguments ->
                    if(it.isSuspend) it.callSuspend(flexInterfaces, action, arguments)
                    else it.call(flexInterfaces, action, arguments)
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

    fun setAllowFileAccessAndUrlAccessInFile(allow: Boolean) {
        settings.allowFileAccess = allow
        settings.allowFileAccessFromFileURLs = allow
        settings.allowUniversalAccessFromFileURLs = allow
    }

    fun setAssetsLoaderUse(use: Boolean, path: String = "/assets/") {
        webViewClient.isAssetLoaderUse = use
        webViewClient.assetLoaderPrefixPath = path
    }

    fun setInterfaceTimeout(timeout: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        options.put(OPT_TIMEOUT, if(timeout != 0 && timeout < 100) 100 else timeout)
    }

    fun setFlexOnLoadWait(timeout: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        options.put(OPT_LOAD_WAIT, timeout)
    }

    fun setInterfaceThreadCount(count: Int) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        tCount = count
    }

    fun setCoroutineContext(coroutineContext: CoroutineContext) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        cContext = coroutineContext
    }

    fun setDependency(inputStream: InputStream) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        dependencies.add(FlexUtil.fileToString(inputStream))
    }

    fun setDependency(reader: Reader) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        dependencies.add(FlexUtil.fileToString(reader))
    }

    fun setDependency(js: String) {
        if(isAfterFirstLoad) {
            throw FlexException(FlexException.ERROR6)
        }
        dependencies.add(js)
    }

    fun evalFlexFunc(funcName: String) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName))
        } else {
            FlexUtil.evaluateJavaScript(this, "!function(){try{const e=\$flex.web.$funcName();if(e instanceof Promise){e.then(e=>{\$flex.flexSuccess('web.$funcName',location.href,e)}).catch(e=>{\$flex.flexException('web.$funcName',location.href,e.toString())})}else{\$flex.flexSuccess('web.$funcName',location.href,e)}}catch(e){\$flex.flexException('web.$funcName',location.href,e.toString())}}();")
        }
    }

    fun evalFlexFunc(funcName: String, response: suspend CoroutineScope.(FlexData) -> Unit) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, response))
        } else {
            val tID = Random.nextInt(10000)
            returnFromWeb[tID] = response
            FlexUtil.evaluateJavaScript(this, "!function(){try{const e=\$flex.web.$funcName();if(e instanceof Promise){e.then(e=>{\$flex.flexSuccess('web.$funcName',location.href,e),\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}).catch(e=>{\$flex.flexException('web.$funcName',location.href,e.toString()),\$flex.flexreturn({TID:$tID,Value:e,Error:!0})})}else{\$flex.flexSuccess('web.$funcName',location.href,e),\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}}catch(e){\$flex.flexException('web.$funcName',location.href,e.toString()),\$flex.flexreturn({TID:$tID,Value:e,Error:!0})}}();")
        }
    }

    fun evalFlexFunc(funcName: String, sendData: Any) {
        if(!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, sendData))
        } else {
            FlexUtil.evaluateJavaScript(this, "!function(){try{const e=\$flex.web.$funcName(${FlexUtil.convertInput(sendData)});if(e instanceof Promise){e.then(e=>{\$flex.flexSuccess('web.$funcName',location.href,e)}).catch(e=>{\$flex.flexException('web.$funcName',location.href,e.toString())})}else{\$flex.flexSuccess('web.$funcName',location.href,e)}}catch(e){\$flex.flexException('web.$funcName',location.href,e.toString())}}();")
        }
    }

    fun evalFlexFunc(funcName: String, sendData: Any, response: suspend CoroutineScope.(FlexData) -> Unit) {
        if (!isFlexLoad) {
            beforeFlexLoadEvalList.add(BeforeFlexEval(funcName, sendData, response))
        } else {
            val tID = Random.nextInt(10000)
            returnFromWeb[tID] = response
            FlexUtil.evaluateJavaScript(this, "!function(){try{const e=\$flex.web.$funcName(${FlexUtil.convertInput(sendData)});if(e instanceof Promise){e.then((e)=>{\$flex.flexSuccess('web.$funcName',location.href,e);\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}).catch((e)=>{\$flex.flexException('web.$funcName',location.href,e.toString());\$flex.flexreturn({TID:$tID,Value:e,Error:!0})})}else{\$flex.flexSuccess('web.$funcName',location.href,e);\$flex.flexreturn({TID:$tID,Value:e,Error:!1})}}catch(e){\$flex.flexException('web.$funcName',location.href,e.toString());\$flex.flexreturn({TID:$tID,Value:e,Error:!0})}}();")
        }
    }

    fun evalFlexFuncWithRespForJava(funcName: String, response: FlexDataListener) {
        evalFlexFunc(funcName) { data -> response.onResponse(data) }
    }

    fun evalFlexFuncWithRespForJava(funcName: String, sendData: Any, response: FlexDataListener) {
        evalFlexFunc(funcName, sendData) { data -> response.onResponse(data) }
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
            flexJsString = flexJsString.replaceFirst("definefromAnd", "'$UNIQUE'")
            flexJsString = flexJsString.replaceFirst("versionFromAnd", "'$VERSION'")
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
            flexJsString = flexJsString.replaceFirst("defineflexfromAnd", FlexUtil.convertInput(internalInterface))
            val device = JSONObject()
            device.put("os","Android")
            device.put("version", Build.VERSION.SDK_INT)
            device.put("model", Build.MODEL)
            flexJsString = flexJsString.replaceFirst("deviceinfoFromAnd", FlexUtil.convertInput(device))
        }
        isAfterFirstLoad = true
        isFlexLoad = false
        FlexUtil.evaluateJavaScript(this, flexJsString)
        dependencies.forEach {
            FlexUtil.evaluateJavaScript(this, it)
        }
    }

    private inner class InternalInterface {
        @JavascriptInterface
        fun flexInterface(input: String) {
            scope.launch {
                try {
                    val data = JSONObject(input)
                    val intName: String = data.getString("intName")
                    val fName: String = data.getString("funName")
                    val args: JSONArray = data.getJSONArray("arguments")
                    try {
                        if (interfaces[intName] != null) { // call interface
                            val value = interfaces[intName]?.let { it(FlexUtil.jsonArrayToFlexData(args)) }
                            if (value is BrowserException) {
                                val reason =
                                    if (value.reason == null) null else "\"${value.reason}\""
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.$fName(false, ${reason})"
                                )
                            } else if (value == null || value == Unit || value is Void) {
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.$fName(true)"
                                )
                            } else {
                                FlexUtil.evaluateJavaScript(
                                    this@FlexWebView,
                                    "\$flex.flex.$fName(true, null, ${FlexUtil.convertInput(value)})"
                                )
                            }
                        } else if (actions[intName] != null) { // call Action
                            actions[intName]?.also { it(FlexAction(fName, this@FlexWebView), FlexUtil.jsonArrayToFlexData(args)) }
                        } else { // call library interface
                            when (intName) {
                                INT_RETURN -> { // flexreturn
                                    val iData = args.getJSONObject(0)
                                    val tID = iData.getInt("TID")
                                    val value = iData.opt("Value")
                                    val error = iData.getBoolean("Error")
                                    if (error) {
                                        val errMsg: String = value?.let { value.toString() } ?: "null"
                                        returnFromWeb[tID]?.also { it(FlexUtil.anyToFlexData(BrowserException(errMsg))) }
                                    } else {
                                        returnFromWeb[tID]?.also { it(FlexUtil.anyToFlexData(value)) }
                                    }
                                    FlexUtil.evaluateJavaScript(
                                        this@FlexWebView,
                                        "\$flex.flex.$fName(true)"
                                    )
                                }
                                INT_LOAD -> { // flexload
                                    if(isFlexLoad) {
                                        FlexUtil.evaluateJavaScript(
                                            this@FlexWebView,
                                            "\$flex.flex.$fName(true)"
                                        )
                                        return@launch
                                    }
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
                                        "\$flex.flex.$fName(true)"
                                    )
                                }
                                EVT_SUC, EVT_EXC, EVT_TIMEOUT, EVT_INIT -> { // events
                                    FlexUtil.evaluateJavaScript(
                                        this@FlexWebView,
                                        "\$flex.flex.$fName(true)"
                                    )
                                    val funcName = args.getString(0)
                                    val url = args.getString(1)
                                    val msg = if(args.length() > 2) args.getString(2) else null
                                    val type: FlexEvent =
                                        when(intName) {
                                            EVT_SUC -> FlexEvent.SUCCESS
                                            EVT_EXC -> FlexEvent.EXCEPTION
                                            EVT_TIMEOUT -> FlexEvent.TIMEOUT
                                            EVT_INIT -> FlexEvent.INIT
                                            else -> FlexEvent.EXCEPTION
                                        }
                                    flexEventList.forEach { _item ->
                                        if(_item.first == type) {
                                            launch(Dispatchers.Main) {
                                                _item.second.listener(this@FlexWebView, type, url, funcName, msg)
                                            }
                                        }
                                    }
                                    flexEventListJava.forEach { _item ->
                                        if(_item.first == type) {
                                            launch(Dispatchers.Main) {
                                                _item.second.onEvent(this@FlexWebView, type, url, funcName, msg)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        FlexUtil.ERR(e)
                        FlexUtil.evaluateJavaScript(
                            this@FlexWebView,
                            "\$flex.flex.$fName(false, \"${e.cause?.message}\")"
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

    private inner class BeforeFlexEval {
        val name:String
        val sendData: Any?
        val response: (suspend CoroutineScope.(FlexData) -> Unit)?

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
        constructor(name: String, response: suspend CoroutineScope.(FlexData) -> Unit) {
            this.name = name
            this.response = response
            sendData = null
        }
        constructor(name: String, sendData:Any, response: suspend CoroutineScope.(FlexData) -> Unit) {
            this.name = name
            this.sendData = sendData
            this.response = response
        }
    }

}
