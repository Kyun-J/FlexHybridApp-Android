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
import app.dvkyun.flexhybridand.FlexFuncInterface;
import app.dvkyun.flexhybridand.FlexInterfaces;
import app.dvkyun.flexhybridand.FlexUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class FlexInterfaceExample extends FlexInterfaces {

    FlexInterfaceExample() {
        this.setInterface("test1", new Function1<JSONArray, Object>() {
            @Override
            public Object invoke(JSONArray arguments) {
                try {
                    return arguments.getInt(0) + 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).setAction("test2", new Function2<FlexAction, JSONArray, Unit>() {
            @Override
            public Unit invoke(final FlexAction flexAction, JSONArray arguments) {
                try {
                    Thread.sleep(1000);
                    flexAction.promiseReturn(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).setInterface("test3", new Function1<JSONArray, Object>() {
            @Override
            public Object invoke(JSONArray arguments) {
                Object[] args = FlexUtil.INSTANCE.convertJSONArray(arguments);
                HashMap<String,Object> obj = (HashMap) args[0];
                obj.put("arrayData", Arrays.toString((Object[]) obj.get("arrayData")));
                Log.i("console", "Receive from web --- " + Arrays.toString(args));
                return null;
            }
        });
    }

    @FlexFuncInterface
    public void test6(JSONArray arguments) {
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
    public void test7(FlexAction action, JSONArray arguments) {
        Log.i("console", "Annotation Action Interface test");
        action.promiseReturn("test success");
    }

}
