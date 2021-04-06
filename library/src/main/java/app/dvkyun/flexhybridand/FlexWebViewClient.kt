package app.dvkyun.flexhybridand

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader

open class FlexWebViewClient: WebViewClient(), FlexWebViewClientInterface {

    private var assetLoader: WebViewAssetLoader? = null

    internal var isAssetLoaderUse = false
    internal var assetLoaderPrefixPath = "/assets/"

    override fun notAllowedUrlLoad(view: WebView, request: WebResourceRequest?, url: String?) {
        FlexUtil.DEBUG("Not Allowed Url Called - $url")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(view is FlexWebView) {
            val requestUrl = request?.url?.toString()
            if(requestUrl != null) {
                view.baseUrl?.also { baseUrl ->
                    if(requestUrl.startsWith(baseUrl)) {
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }
                var isFoundAllowUrl = view.allowUrlList.size == 0
                view.allowUrlList.forEach {
                    if(requestUrl.startsWith(it)) {
                        isFoundAllowUrl = true
                        return@forEach
                    }
                }
                if(!isFoundAllowUrl) {
                    notAllowedUrlLoad(view, request, requestUrl)
                    return false
                }
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    @Suppress("DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if(view is FlexWebView) {
            if(url != null) {
                view.baseUrl?.also { baseUrl ->
                    if(url.startsWith(baseUrl)) {
                        return super.shouldOverrideUrlLoading(view, url)
                    }
                }
                var isFoundAllowUrl = false
                view.allowUrlList.forEach {
                    if(url.startsWith(it)) {
                        isFoundAllowUrl = true
                        return@forEach
                    }
                }
                if(!isFoundAllowUrl) {
                    notAllowedUrlLoad(view, url = url)
                    return false
                }
            }
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if(view is FlexWebView && url != null) {
            val baseUrl = view.baseUrl
            if(baseUrl == null || url.startsWith(baseUrl)) {
                view.flexInitInPage()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        if(isAssetLoaderUse && view != null && url != null) {
            if(assetLoader == null) {
                assetLoader = WebViewAssetLoader
                    .Builder()
                    .addPathHandler(
                        assetLoaderPrefixPath,
                        WebViewAssetLoader.AssetsPathHandler(view.context)
                    ).build()
            }
            return assetLoader?.shouldInterceptRequest(Uri.parse(url))
        }
        assetLoader = null
        return super.shouldInterceptRequest(view, url)
    }

    @RequiresApi(21)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if(isAssetLoaderUse && view != null && request?.url != null) {
            if(assetLoader == null) {
                assetLoader = WebViewAssetLoader
                    .Builder()
                    .addPathHandler(
                        assetLoaderPrefixPath,
                        WebViewAssetLoader.AssetsPathHandler(view.context)
                    ).build()
            }
            return assetLoader?.shouldInterceptRequest(request.url)
        }
        assetLoader = null
        return super.shouldInterceptRequest(view, request)
    }
}

private interface FlexWebViewClientInterface {
    fun notAllowedUrlLoad(view: WebView, request: WebResourceRequest? = null, url: String? = null)
}
