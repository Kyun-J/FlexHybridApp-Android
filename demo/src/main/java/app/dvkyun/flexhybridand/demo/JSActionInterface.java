package app.dvkyun.flexhybridand.demo;

import android.webkit.JavascriptInterface;

import app.dvkyun.flexhybridand.FlexJSAction;
import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.jvm.functions.Function0;

public class JSActionInterface {
    private FlexWebView mWebView;

    JSActionInterface(FlexWebView webView) {
        mWebView = webView;
    }

    @JavascriptInterface
    public FlexJSAction testAction(final int input) {
        final FlexJSAction action = new FlexJSAction(mWebView);
        //do something in other thread
        //{
            if (action.isReady()) {
                action.send(input + 1);
            }
        //}
        action.setReadyListener(new Function0<Object>() {
            @Override
            public Object invoke() {
                return input + 1;
            }
        });
        return action;
    }
}
