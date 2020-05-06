package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FlexWebView flexWebView = findViewById(R.id.flex_web_view);

        flexWebView.setWebContentsDebuggingEnabled(true);
        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.getSettings().setTextZoom(200);

        flexWebView.setInterface("test5", new Function1<JSONArray, Object>() {
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
}
