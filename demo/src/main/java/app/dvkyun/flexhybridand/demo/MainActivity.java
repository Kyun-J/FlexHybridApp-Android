package app.dvkyun.flexhybridand.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexArguments;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexEvent;
import app.dvkyun.flexhybridand.FlexWebView;
import app.dvkyun.flexhybridand.FlexWebViewClient;
import app.dvkyun.flexhybridand.forjava.FlexDataListener;
import app.dvkyun.flexhybridand.forjava.FlexListenerForJava;
import app.dvkyun.flexhybridand.forjava.InvokeAction;
import app.dvkyun.flexhybridand.forjava.InvokeFlexVoid;
import kotlinx.coroutines.Dispatchers;

public class MainActivity extends AppCompatActivity {

    private  FlexWebView flexWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flexWebView = findViewById(R.id.flex_web_view);
        flexWebView.setWebViewClient(new FlexWebViewClient() {
            @Override
            public void notAllowedUrlLoad(@NotNull WebView view, @Nullable WebResourceRequest request, @Nullable String url) {
                super.notAllowedUrlLoad(view, request, url);
//                flexWebView.loadUrl("https://appassets.androidplatform.net/assets/html/test.html");
            }
        });
        flexWebView.evalFlexFunc("webtest","webtest");
        flexWebView.addFlexEventListenerForJava(new FlexListenerForJava() {
            @Override
            public void onEvent(@NotNull FlexWebView view, @NotNull FlexEvent type, @NotNull String url, @NotNull String funcName, @Nullable String msg) {
                Log.i("listener", "\ntype: " + type.name() +
                        "\nurl: " + url +
                        "\nfuncName: " + funcName +
                        "\nmsg: " + msg);
            }
        });

        flexWebView.setInterfaceTimeout(3000);
        flexWebView.setInterfaceThreadCount(63);
//        flexWebView.setCoroutineContext(Dispatchers.getIO());
        flexWebView.getSettings().setTextZoom(250);

        flexWebView.setActionForJava("test4", null, new InvokeAction() {
            @Override
            public void invoke(@NotNull FlexAction action, @NotNull FlexArguments arguments) {
                HashMap<String,Object> data = new HashMap<>();
                data.put("intData",1);
                data.put("StringData","test\ntest\n");
                data.put("doubleData",1.0000000001);
                data.put("boolData", false);
                Object[] arrayData = new Object[3];
                arrayData[0] = 1;
                arrayData[1] = 22;
                arrayData[2] = 333;
                data.put("arrayData", arrayData);
                HashMap<String,Object> objectData = new HashMap<>();
                objectData.put("o1",null);
                objectData.put("o2",999.9999);
                objectData.put("o3","dataO3");
                data.put("objectData", objectData);
                Log.i("console","Send to web --- " + data.toString());
                action.promiseReturn(data);
            }
        }).voidInterfaceForJava("test5", null, new InvokeFlexVoid() {
            @Override
            public void invoke(@NotNull FlexArguments arguments) {
                flexWebView.evalFlexFuncWithRespForJava("webtest", "hi! $flex!", new FlexDataListener() {
                    @Override
                    public void onResponse(@NotNull FlexData response) {
                        FlexData[] arr = response.asArray();
                        Log.i("console", "Receive from web --- " + arr[0].asString() + arr[1].asInt() + arr[2].asMap().toString());
                    }
                });
            }
        });

        flexWebView.addFlexInterface(new FlexInterfaceExample());
        flexWebView.addFlexInterface(new FlexInterfaceExample2());

//        flexWebView.setBaseUrl("file:///android_asset");
//        flexWebView.addAllowUrl(".google.com", true);
//        flexWebView.setAllowFileAccessAndUrlAccessInFile(true);
//        flexWebView.loadUrl("file:///android_asset/html/test.html");

        flexWebView.setBaseUrl("appassets.androidplatform.net");
        flexWebView.setAssetsLoaderUse(true, "/assets/");
        flexWebView.addAllowUrl(".google.com", false);
        flexWebView.loadUrl("https://appassets.androidplatform.net/assets/html/test.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        KtObject.INSTANCE.setNowAppContext(this);

    }
}
