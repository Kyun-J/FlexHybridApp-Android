package app.dvkyun.flexhybridand.demo;

import android.util.Log;
import java.util.HashMap;
import app.dvkyun.flexhybridand.FlexData;
import app.dvkyun.flexhybridand.FlexInterfaces;
import app.dvkyun.flexhybridand.forjava.InvokeFlex;

public class FlexInterfaceExample extends FlexInterfaces {

    FlexInterfaceExample() {
        this.setInterfaceForJava("test1", null, (InvokeFlex) arguments -> arguments.get(0).asInt() + 1
        ).setActionForJava("test2", 10000, (action, arguments) -> {
            try {
                Thread.sleep(1000);
                action.resolveVoid();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).setInterfaceForJava("test3", null, arguments -> {
            HashMap<String, FlexData> obj = (HashMap) arguments.get(0).asMap();
            Log.i("console", "Receive from web");
            Log.i("console", "stringData --- " + obj.get("stringData").asString());
            Log.i("console", "intData --- " + obj.get("intData").asInt());
            Log.i("console", "floatData --- " + obj.get("floatData").asDouble());
            Log.i("console", "boolData --- " + obj.get("boolData").asBoolean());
            Log.i("console", "arrayData --- " + obj.get("arrayData").asArray().toString());
            Log.i("console", "nullData --- " + obj.get("nullData").asMap());
            Log.i("console", "objectData --- " + obj.get("objectData").asMap().toString());
        });
    }
}
