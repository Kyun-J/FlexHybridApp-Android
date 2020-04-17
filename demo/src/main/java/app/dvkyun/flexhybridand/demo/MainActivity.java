package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.dvkyun.flexhybridand.FlexWebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlexWebView flexWebView = findViewById(R.id.flex_web_view);

        flexWebView.setWebContentsDebuggingEnabled(true);
        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.addJsInterface(new JSInterface(flexWebView));
        flexWebView.addJsInterface(new JSInterfaceKotlin(flexWebView));
        flexWebView.loadUrl("file:///android_asset/html/test.html");
    }
}
