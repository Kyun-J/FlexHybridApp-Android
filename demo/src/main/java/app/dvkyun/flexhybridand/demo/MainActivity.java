package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.HashMap;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private  FlexWebView flexWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flexWebView = findViewById(R.id.flex_web_view);

        flexWebView.setWebContentsDebuggingEnabled(true);
        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.getSettings().setTextZoom(250);

        flexWebView.setAction("test4", new Function2<FlexAction, JSONArray, Unit>() {
            @Override
            public Unit invoke(FlexAction flexAction, JSONArray arguments) {
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
                objectData.put("o1",1);
                objectData.put("o2",999.9999);
                objectData.put("o3","dataO3");
                data.put("objectData", objectData);
                Log.i("console","Send to web --- " + data.toString());
                flexAction.promiseReturn(data);
                return null;
            }
        }).setInterface("test5", new Function1<JSONArray, Object>() {
            @Override
            public Object invoke(JSONArray objects) {
                flexWebView.evalFlexFunc("webtest", "hi! $flex!", new Function1<Object, Unit>() {
                    @Override
                    public Unit invoke(Object response) {
                        Log.i("console", "Receive from web --- " + response.toString());
                        return null;
                    }
                });
                return null;
            }
        });

        flexWebView.addFlexInterface(new FlexInterfaceExample());
        flexWebView.loadUrl("file:///android_asset/html/test.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        KtObject.INSTANCE.setNowAppContext(this);

    }
}
