package app.dvkyun.flexhybridand.demo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;

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
        }).setAction("test4", new Function2<FlexAction, JSONArray, Unit>() {
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
        });
    }

    @FlexFuncInterface
    public void test6(JSONArray arguments) {
        Log.i("ttt","suc");
    }

    @FlexActionInterface
    public void test7(FlexAction action, JSONArray arguments) {
        Log.i("ttt","suc2");
    }

}
