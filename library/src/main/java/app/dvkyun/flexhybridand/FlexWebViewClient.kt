package app.dvkyun.flexhybridand

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader

open class FlexWebViewClient: WebViewClient() {

    private var assetLoader: WebViewAssetLoader? = null

    internal var isAssetLoaderUse = false
    internal var assetLoaderPrefixPath = "/assets/"

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
    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        if(isAssetLoaderUse) {
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
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        if(isAssetLoaderUse) {
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
