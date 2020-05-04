package app.dvkyun.flexhybridand

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader

class FlexWebView: WebView {

    private val mActivity: Activity? = FlexUtil.getActivity(context)
    private var flexJsString: String
    private val interfaces: HashMap<String,(Array<Any?>?) -> Any?> = HashMap()
    private val actions: HashMap<String,FlexAction> = HashMap()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isAfterFirstLoad = false
    internal var baseUrl: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        if(mActivity == null) throw FlexException(FlexException.ERROR1)
        flexJsString = try {
            val reader = BufferedReader(context.assets.open("FlexHybridAnd.js").reader())
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
            sb.toString()
        } catch (e: java.lang.Exception) {
            throw FlexException(e)
        }
        webChromeClient = FlexWebChromeClient(mActivity)
        webViewClient = FlexWebViewClient()
        super.addJavascriptInterface(FlexInterface(), "flexdefine")
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
        settings.enableSmoothTransition()
        settings.javaScriptCanOpenWindowsAutomatically = true
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, true)
        }
    }

    fun setBaseUrl(url: String) {
        if(baseUrl != null) {
            throw FlexException(FlexException.ERROR7)
        }
        baseUrl = url
    }

    fun getBaseUrl(): String? = baseUrl

    fun setInterface(name: String, lambda: (Array<Any?>?) -> Any?) {
        if(isAfterFirstLoad) {
            //error
        }
        if(interfaces[name] != null) {
            //error
        }
        interfaces[name] = lambda
    }

    fun setAction(name: String, action: FlexAction) {
        if(isAfterFirstLoad) {
            //error
        }
        if(actions[name] != null) {
            //error
        }
        actions[name] = action
    }

    fun getAction(name: String): FlexAction? {
        return actions[name]
    }

    fun evalFlexFunc(funcName: String) {
        FlexUtil.evaluateJavaScript(this,"window.\$flex.web.$funcName()")
    }

    fun evalFlexFunc(funcName: String, returnListener: () -> Void) {
        FlexUtil.evaluateJavaScript(this,"window.\$flex.web.$funcName()")
    }

//    fun evalFlexFunc(funcName: String, argument: Any?) {
//        FlexUtil.evaluateJavaScript(this,"window.\$flex.web.$funcName(${FlexUtil.convertValue(argument)})")
//    }

    fun setToGlobalFlexWebView(set: Boolean) {
        if(set) FlexUtil.globalFlexWebView = this
    }

    override fun getWebChromeClient(): FlexWebChromeClient {
        return super.getWebChromeClient() as FlexWebChromeClient
    }

    override fun setWebChromeClient(client: WebChromeClient) {
        if(client !is FlexWebChromeClient) throw FlexException(FlexException.ERROR3)
        super.setWebChromeClient(client)
    }

    override fun getWebViewClient(): FlexWebViewClient {
        return super.getWebViewClient() as FlexWebViewClient
    }

    override fun setWebViewClient(client: WebViewClient) {
        if(client !is FlexWebViewClient) throw FlexException(FlexException.ERROR3)
        super.setWebViewClient(client)
    }

    override fun loadUrl(url: String?) {
        if(baseUrl == null) throw FlexException(FlexException.ERROR2)
        super.loadUrl(url)
    }

    override fun loadUrl(url: String?, additionalHttpHeaders: MutableMap<String, String>?) {
        if(baseUrl == null) throw FlexException(FlexException.ERROR2)
        super.loadUrl(url, additionalHttpHeaders)
    }

    @SuppressLint("JavascriptInterface")
    override fun addJavascriptInterface(`object`: Any?, name: String?) {
        if(name == "flexdefine") {
            //error
        }
        super.addJavascriptInterface(`object`, name)
    }

    internal fun flexInitInPage() {
        if(!isAfterFirstLoad) {
            val keys = StringBuilder()
            if(interfaces.size > 0 && actions.size > 0) {
                keys.append("[\"")
                keys.append(interfaces.keys.joinToString(separator = "\",\""))
                keys.append("\",\"")
                keys.append(actions.keys.joinToString(separator = "\",\""))
                keys.append("\"]")
            } else if(interfaces.size > 0) {
                keys.append("[\"")
                keys.append(interfaces.keys.joinToString(separator = "\",\""))
                keys.append("\"]")
            } else if(actions.size > 0) {
                keys.append("[\"")
                keys.append(actions.keys.joinToString(separator = "\",\""))
                keys.append("\"]")
            } else {
                keys.append("[]")
            }
            flexJsString = flexJsString.replaceFirst("keysfromAnd",keys.toString())
        }
        isAfterFirstLoad = true
        FlexUtil.evaluateJavaScript(this, flexJsString)
    }

    inner class FlexInterface {
        @JavascriptInterface
        fun flexInterface(input: String) {
            scope.launch {
                try {
                    val data = JSONObject(input)
                    val intName : String = data.getString("intName")
                    val fName : String = data.getString("funName")
                    val args : JSONArray? = data.getJSONArray("arguments")
                    if(interfaces[intName] != null) {
                        val value = interfaces[intName]?.invoke(FlexUtil.convertJSONArray(args))
                        if(value == null) {
                            FlexUtil.evaluateJavaScript(this@FlexWebView, "window.${fName}()")
                        } else {
                            FlexUtil.evaluateJavaScript(this@FlexWebView, "window.${fName}(${FlexUtil.convertValue(value)})")
                        }
                    } else if(actions[intName] != null) {
                        val action = actions[intName]!!
                        action.flexWebView = this@FlexWebView
                        action.doAction.invoke(action, FlexUtil.convertJSONArray(args))
                        action.setFunName(fName)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
