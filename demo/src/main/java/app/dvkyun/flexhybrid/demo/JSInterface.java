package app.dvkyun.flexhybrid.demo;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import app.dvkyun.flexhybrid.FlexJSCall;
import app.dvkyun.flexhybrid.FlexWebView;

public class JSInterface {

    private WebView mWebView;
    private ThreadPoolExecutor mExecutor;

    JSInterface(WebView webView) {
        mWebView = webView;
        mExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new LinkedBlockingDeque());
    }

    @JavascriptInterface
    public FlexJSCall testCall(final String input) {
        return new FlexJSCall(mWebView).call(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
               Thread.sleep(1000);
               mWebView.post(new Runnable() {
                   @Override
                   public void run() {
                       ((FlexWebView)mWebView).evalFlexFunc("receive", input.concat("isNative"));
                   }
               });
               return Integer.parseInt(input) + 1;
            }
        });
    }

    @JavascriptInterface
    public FlexJSCall testCall2(final String input) {
        return new FlexJSCall(mWebView)
                .setExecutor(mExecutor)
                .call(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.sleep(1000);
                        return input;
                    }
                });
    }
}
