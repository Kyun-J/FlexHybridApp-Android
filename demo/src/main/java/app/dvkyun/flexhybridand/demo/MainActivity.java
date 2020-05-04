package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlexWebView flexWebView = findViewById(R.id.flex_web_view);

        flexWebView.setWebContentsDebuggingEnabled(true);
        flexWebView.setToGlobalFlexWebView(true);
        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.setInterface("testCall", new Function1<Object[], Object>() {
            @Override
            public Object invoke(Object[] objects) {
                return ((int) objects[0]) + 1;
            }
        });
        flexWebView.loadUrl("file:///android_asset/html/test.html");
    }
}
