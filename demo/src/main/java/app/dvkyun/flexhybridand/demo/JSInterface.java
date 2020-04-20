package app.dvkyun.flexhybridand.demo;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import app.dvkyun.flexhybridand.FlexJSCall;
import app.dvkyun.flexhybridand.FlexWebView;

public class JSInterface {

    private WebView mWebView;
    private ThreadPoolExecutor mExecutor;

    JSInterface(WebView webView) {
        mWebView = webView;
        mExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new LinkedBlockingDeque());
    }

    @JavascriptInterface
    public FlexJSCall testCall(final int input) {
        return new FlexJSCall().call(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
               Thread.sleep(1000);
               mWebView.post(new Runnable() {
                   @Override
                   public void run() {
                       ((FlexWebView)mWebView).evalFlexFunc("receive", Integer.toString(input).concat("isNative"));
                   }
               });
               return input + 1;
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
