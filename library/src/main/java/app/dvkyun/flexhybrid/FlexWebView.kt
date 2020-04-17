package app.dvkyun.flexhybrid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import java.io.BufferedReader

class FlexWebView: WebView {

    private val mActivity: Activity? = FlexStatic.getActivity(context)
    internal var baseUrl: String? = null
    internal val flexJsString: String
    private var interfaceCount = 0

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
        baseUrl = url
    }

    fun getBaseUrl(): String? {
        return baseUrl
    }

    fun evalFlexFunc(funcName: String, prompt: Any?) {
        FlexStatic.evaluateJavaScript(this,"window.\$flex.web.$funcName('${prompt.toString()}')")
    }

    fun evalFlexFunc(funcName: String) {
        FlexStatic.evaluateJavaScript(this,"window.\$flex.web.$funcName()")
    }

    @SuppressLint("JavascriptInterface")
    fun addJsInterface(cls: Any) {
        addJavascriptInterface(cls, "flexAnd$interfaceCount")
        interfaceCount += 1
    }

    override fun getWebChromeClient(): FlexWebChromeClient {
        return super.getWebChromeClient() as FlexWebChromeClient
    }

    override fun setWebChromeClient(client: WebChromeClient) {
        if(client !is FlexWebChromeClient) throw FlexException(FlexException.ERROR6)
        super.setWebChromeClient(client)
    }

    override fun getWebViewClient(): FlexWebViewClient {
        return super.getWebViewClient() as FlexWebViewClient
    }

    override fun setWebViewClient(client: WebViewClient) {
        if(client !is FlexWebViewClient) throw FlexException(FlexException.ERROR6)
        super.setWebViewClient(client)
    }

    override fun loadUrl(url: String?) {
        if(baseUrl == null) throw FlexException(FlexException.ERROR5)
        super.loadUrl(url)
    }

    override fun loadUrl(url: String?, additionalHttpHeaders: MutableMap<String, String>?) {
        if(baseUrl == null) throw FlexException(FlexException.ERROR5)
        super.loadUrl(url, additionalHttpHeaders)
    }
}