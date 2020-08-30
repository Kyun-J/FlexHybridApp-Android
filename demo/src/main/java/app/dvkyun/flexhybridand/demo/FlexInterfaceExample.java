package app.dvkyun.flexhybridand.demo;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexActionInterface;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexException;
import app.dvkyun.flexhybridand.FlexFuncInterface;
import app.dvkyun.flexhybridand.FlexInterfaces;
import app.dvkyun.flexhybridand.FlexBrowserErr;
import app.dvkyun.flexhybridand.FlexUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class FlexInterfaceExample extends FlexInterfaces {

    FlexInterfaceExample() {
        this.setInterface("test1", new Function1<FlexData[], Integer>() {
            @Override
            public Integer invoke(FlexData[] arguments) {
                return arguments[0].asInt() + 1;
            }
        }).setAction("test2", new Function2<FlexAction, FlexData[], Unit>() {
            @Override
            public Unit invoke(final FlexAction flexAction, FlexData[] arguments) {
                try {
                    Thread.sleep(1000);
                    flexAction.resolveVoid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).setInterface("test3", new Function1<FlexData[], Void>() {
            @Override
            public Void invoke(FlexData[] arguments) {
                HashMap<String, FlexData> obj = (HashMap) arguments[0].asMap();
                Log.i("console", "Receive from web");
                Log.i("console", "stringData --- " + obj.get("stringData").asString());
                Log.i("console", "intData --- " + obj.get("intData").asInt());
                Log.i("console", "floatData --- " + obj.get("floatData").asDouble());
                Log.i("console", "boolData --- " + obj.get("boolData").asBoolean());
                Log.i("console", "arrayData --- " + obj.get("arrayData").asArray().toString());
                Log.i("console", "objectData --- " + obj.get("objectData").asMap().toString());
                return null;
            }
        });
    }

    @FlexFuncInterface
    public void test6(FlexData[] arguments) {
        ((Activity) Objects.requireNonNull(KtObject.INSTANCE.getNowAppContext())).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Objects.requireNonNull(KtObject.INSTANCE.getNowAppContext()))
                        .setTitle("DialogTest")
                        .setMessage("AlertDialogSuccess!!")
                        .create()
                        .show();
            }
        });
    }

    @FlexActionInterface
    public void test7(FlexAction action, FlexData[] arguments) {
        Log.i("console", "Annotation Action Interface test");
        action.promiseReturn("test success");
    }

    @FlexFuncInterface
    public FlexBrowserErr test8(FlexData[] arguments) {
        return new FlexBrowserErr("reject test");
    }

    @FlexActionInterface
    public void test9(FlexAction action, FlexData[] arguments) {
        action.reject("action reject test");
    }

}
