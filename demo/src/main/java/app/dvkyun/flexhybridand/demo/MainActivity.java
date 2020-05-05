package app.dvkyun.flexhybridand.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexWebView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private HandlerThread testThread = new HandlerThread("testThread");
    private Handler handler = new Handler(testThread.getLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlexWebView flexWebView = findViewById(R.id.flex_web_view);

        flexWebView.setWebContentsDebuggingEnabled(true);
        flexWebView.setBaseUrl("file:///android_asset");
        flexWebView.setInterface("testCall", new Function1<Object[], Object>() {
            @Override
            public Object invoke(Object[] arguments) {
                return ((int) arguments[0]) + 1;
            }
        });
        flexWebView.setAction("testAction", new Function2<FlexAction, Object[], Unit>() {
            @Override
            public Unit invoke(final FlexAction flexAction, Object[] arguments) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            String[] response = new String[2];
                            response[0] = "1";
                            response[1] = "010";
                            flexAction.promiseReturn(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return null;
            }
        });

        flexWebView.loadUrl("file:///android_asset/html/test.html");
    }
}
