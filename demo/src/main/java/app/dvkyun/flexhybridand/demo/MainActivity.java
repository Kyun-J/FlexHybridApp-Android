package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

public class MainActivity extends AppCompatActivity {

    private  FlexWebView flexWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flexWebView = findViewById(R.id.flex_web_view);
        flexWebView.evalFlexFunc("webtest","aaa");

        flexWebView.setWebContentsDebuggingEnabled(true);

        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.setInterfaceTimeout(3000);
        flexWebView.setInterfaceThreadCount(Runtime.getRuntime().availableProcessors());

        flexWebView.getSettings().setTextZoom(250);

        flexWebView.setAction("test4", new Function3<FlexAction, FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexAction flexAction, FlexData[] flexData, Continuation<? super Unit> continuation) {
                HashMap<String,Object> data = new HashMap<>();
                data.put("intData",1);
                data.put("StringData","test");
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
                flexAction.promiseReturn(data);
                return null;
            }
        }).voidInterface("test5", new Function2<FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexData[] flexData, Continuation<? super Unit> continuation) {
                flexWebView.evalFlexFunc("webtest", "hi! $flex!", new Function2<FlexData, Continuation<? super Unit>, Object>() {
                    @Override
                    public Object invoke(FlexData response, Continuation<? super Unit> continuation) {
                        FlexData[] arr = response.asArray();
                        Log.i("console", "Receive from web --- " + arr[0].asString() + arr[1].asInt() + arr[2].asMap().toString() );
                        return null;
                    }
                });
                return null;
            }
        });

        flexWebView.addFlexInterface(new FlexInterfaceExample());
        flexWebView.addFlexInterface(new FlexInterfaceExample2());
        flexWebView.loadUrl("file:///android_asset/html/test.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        KtObject.INSTANCE.setNowAppContext(this);

    }
}
