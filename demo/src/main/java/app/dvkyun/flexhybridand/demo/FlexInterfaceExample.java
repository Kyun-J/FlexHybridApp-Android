package app.dvkyun.flexhybridand.demo;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import app.dvkyun.flexhybridand.FlexAction;
import app.dvkyun.flexhybridand.FlexArguments;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexInterfaces;
import app.dvkyun.flexhybridand.forjava.InvokeAction;
import app.dvkyun.flexhybridand.forjava.InvokeFlex;
import app.dvkyun.flexhybridand.forjava.InvokeFlexVoid;

public class FlexInterfaceExample extends FlexInterfaces {

    FlexInterfaceExample() {
        this.intInterfaceForJava("test1", null, new InvokeFlex<Integer>() {
            @Override
            public Integer invoke(@NotNull FlexArguments arguments) {
                return arguments.get(0).asInt() + 1;
            }
        }).setActionForJava("test2", 10000, new InvokeAction() {
            @Override
            public void invoke(@NotNull FlexAction action, @NotNull FlexArguments arguments) {
                try {
                    Thread.sleep(1000);
                    action.resolveVoid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).voidInterfaceForJava("test3", null, new InvokeFlexVoid() {
            @Override
            public void invoke(@NotNull FlexArguments arguments) {
                HashMap<String, FlexData> obj = (HashMap) arguments.get(0).asMap();
                Log.i("console", "Receive from web");
                Log.i("console", "stringData --- " + obj.get("stringData").asString());
                Log.i("console", "intData --- " + obj.get("intData").asInt());
                Log.i("console", "floatData --- " + obj.get("floatData").asDouble());
                Log.i("console", "boolData --- " + obj.get("boolData").asBoolean());
                Log.i("console", "arrayData --- " + obj.get("arrayData").asArray().toString());
                Log.i("console", "nullData --- " + obj.get("nullData").asMap());
                Log.i("console", "objectData --- " + obj.get("objectData").asMap().toString());
            }
        });
    }
}
