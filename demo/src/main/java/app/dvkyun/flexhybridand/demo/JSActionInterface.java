package app.dvkyun.flexhybridand.demo;

import android.util.Log;
import android.webkit.JavascriptInterface;

import app.dvkyun.flexhybridand.FlexJSAction;
import app.dvkyun.flexhybridand.FlexWebView;

public class JSActionInterface {
    private FlexWebView mWebView;

    JSActionInterface(FlexWebView webView) {
        mWebView = webView;
    }

    @JavascriptInterface
    public FlexJSAction testAction(final int input) {
        final FlexJSAction action = new FlexJSAction(mWebView);
        someOtherWork(action, input);
        return action;
    }

    void someOtherWork(FlexJSAction action, int input) {
        action.promiseReturn(input + 1);
    }

    @JavascriptInterface
    public FlexJSAction testAny(final Object input) {
        final FlexJSAction action = new FlexJSAction(mWebView);
        action.promiseReturn(null);
        return action;
    }

}
