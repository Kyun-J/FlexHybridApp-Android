package app.dvkyun.flexhybridand.demo;

import android.util.Log;
import java.util.HashMap;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexInterfaces;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

public class FlexInterfaceExample extends FlexInterfaces {

    FlexInterfaceExample() {
        this.intInterface("test1", new Function2<FlexData[], Continuation<? super Integer>, Object>() {
            @Override
            public Object invoke(FlexData[] arguments, Continuation<? super Integer> continuation) {
                return arguments[0].asInt() + 1;
            }
        }).setAction("test2", new Function3<FlexAction, FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexAction flexAction, FlexData[] flexData, Continuation<? super Unit> continuation) {
                try {
                    Thread.sleep(1000);
                    flexAction.resolveVoid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).voidInterface("test3", new Function2<FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexData[] arguments, Continuation<? super Unit> continuation) {
                HashMap<String, FlexData> obj = (HashMap) arguments[0].asMap();
                Log.i("console", "Receive from web");
                Log.i("console", "stringData --- " + obj.get("stringData").asString());
                Log.i("console", "intData --- " + obj.get("intData").asInt());
                Log.i("console", "floatData --- " + obj.get("floatData").asDouble());
                Log.i("console", "boolData --- " + obj.get("boolData").asBoolean());
                Log.i("console", "arrayData --- " + obj.get("arrayData").asArray().toString());
                Log.i("console", "nullData --- " + obj.get("nullData").asMap());
                Log.i("console", "objectData --- " + obj.get("objectData").asMap().toString());
                return null;
            }
        });
    }

}
