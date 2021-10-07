[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

Including simplifying the interface between Webview and Native and asynchronous processing,
It is a library that provides multiple convenience features for Webview use.

# How to add libraries

**minSdkVersion 23**  
**Minimum Android WebviewVersion 55**

1. jitpack

Add the following to gradle's repositories

```Gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Add the following dependencies in gradle

```Gradle
dependencies {
    implementation 'com.github.Kyun-J:FlexHybridApp-Android:latest-version'
}
```

# Key Features

1. It has similar development rules and functions to the [iOS version](https://github.com/Kyun-J/FlexHybridApp-iOS).
2. The interface between Webview and Native operates asynchronously.
   1. Called and returned as **Promise** from the Web.
   2. In Native, it operates as **Coroutine**.
3. You can define behavior on interface with **Kotlin's lambda**.
4. In addition to basic data types, **Array, Map, Model** can be used.
5. By setting the number of threads the interface will run on, you can **parallelize** multiple operations.
6. You can prevent **Native calls from unwanted sites** by designating URLs that allow interface operation.

and various other functions exist.

# Basic usage of interface

## Web to Nativew

### Interface registration

```kt
// in kotlin
flexWebView.setInterface("funcName") { args ->
    "received from web - ${args[0]?.toString() ?: "no value"}, return to web - ${100}"
}
flexWebView.loadUrl(someUrl)
```

### Interface invoke

```js
// in js
const test = async (req) => {
  const res = await $flex.funcName(req);
  console.log(res); // received from web - 200, return to web - 100
};
test(200);
```

## Natvie to Web

### Interface registration

```js
// in js
window.onFlexLoad = () => {
  $flex.web.funcName = async (req) => {
    return await new Promise((resolve) => {
      setTimeout(() => resolve(`received from web - ${req}`), 100);
    });
  };
};
```

### Interface invoke

```kt
// in kotlin
flexWebView.evalFlexFunc("funcName", "sendData") { response ->
    Log.i("WebToNative", response.toString()) // received from web - sendData
}
```

# Advanced usage of interface

## FlexData and FlexArguemtns

The data received from the Web is converted into a FlexData object for TypeSafe loading.  
In Web to native interface, Arguments passed from Web functions are converted into FlexArguemnts objects.  
FlexArguments is an Array of FlexData.

```js
// in js
$flex.funcName("test1", 2, 3.1, true, [0, 1, 2], { test: "object" });
```

```kt
//in kotlin
flexWebView.setInterface("funcName") { args ->
    if (args == null) return@setInterface

    val first = args[0].asString() // "test"
    val second = args[1].asInt() // 2
    val third = args[2].asDouble() // 3.1
    val fourth = args[3].asBoolean() // true
    val fifth = args[4].asArray() // array of 0, 1, 2
    val sixth = args[5].asMap() // map of first key - test, value - "object"

    val argsArray: Array<FlexData> = args.toArray()
    val argsList: List<FlexData> = args.toList()
}
```

## Action Object

In the Web to Native interface, in code other than the specified lambda code block
A way to return a value.

```kt
var mAction: FlexAction? = null

flexWebView.setAction("actionTest") { action, args ->
    val mLocationRequest = getLocationRequest()
    val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (mFusedLocationClient == null) {
        action.reject("location client is null")
        return@setAction
    }
    mAction = action
    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
}

private val mLocationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        var mLastLocation: Location = locationResult.lastLocation
        val latitude = mLastLocation.latitude.toString()
        mAction?.promiseReturn(latitude)
    }
}
```

Can return a value from the code at any location through the Action object.
At this time, the web will be in **pending** state until a response occurs.
Also, the Action object once responded to cannot be used again.

## Model Class

Data to be used for the interface can be used as a Model Class.  
In this case, the rules below apply.

1. Model Class **must Inheritance** FlexType class.
2. In Web, it is converted into object form.
3. Array cannot be used as a member variable of Class, and only classes that inherit Iterable (such as ArrayList) can be used when implementing a list.
4. When receiving Model Class as Arguments in Native, only one object corresponding to the Model in Web should be delivered.

```kt
// in kotlin
data class TestModel(
    val name: String,
    val data2: TestModel2
): FlexType

data class TestModel2(
    val testInt: Int
): FlexType

data class ArgsTestModel(
    val testModel: TestModel
): FlexType

flexWebView.setInterface("modelTest") { args ->
    TestModel("test", TestModel2(2000))
}

flexWebView.typeInterface("modelArgsTest") { req: ArgsTestModel ->
    Log.i("ModelTest", req.data2.testInt)
}
```

```js
// in js
const test = async () => {
  const model = await $flex.modelTest(); // model is { name: 'test', data2: { testInt: 2000 } }
  await $flex.modelArgsTest(model);
};
test();
```

## Separately declare lambda

This is a function for easily declaring and cleaning up lambdas that define behavior in interfaces.  
It can be easily used through a function in which a returnable type-specific lambda is defined in the companion object of the FlexLambda class.  
At this time, the type of args must be designated as FlexArguemts or Model Class.

```kt
val lambdaVar = FlexLambda.list { args: FlexArguments ->
    ... some job
}
val lambdaActionVar = FlexLambda.action { action, args: FlexArguments ->
    ... come job
}

flexWebView.setInterface("lamdaVarTest", lambda = lambdaVar)
flexWebView.setAction("lambdaActionVarTest", action = lambdaActionVar)
```

## Web to native interface timeout setting

Since the interface operates asynchronously, there is a risk of falling into an infinite pending state in the Web.  
So it can set the timeout of the interface, and when the timeout occurs, it will switch to reject.  
If timeout is not set, the default value is 60000ms.

### default timeout setting

You can specify a default value for timeout. If a per-interface timeout is not set, its value is set by default.  
If set to 0, no timeout occurs and waits indefinitely.  
The unit is ms.

```kt
val timeout = 1000
flexWebView.setInterfaceTimeout(timeout)
```

### Specify timeout for each interface

When configuring the interface, you can specify the timeout of the interface.  
If set to 0, no timeout occurs and waits indefinitely.  
The unit is ms.

```kt
val timeout = 200
fletWebView.setInterface("funcName", timeout) {}
```

## Interface thread settings

You can set the number of threads or CoroutineContext on which the interface will run.  
All interfaces in Native operate within the CoroutineScope set by the corresponding thread pool/CoroutineContext.  
If you specify both settings at the same time, the CoroutineContext setting takes precedence.  
If not set, it acts as a CoroutineScope set to the number of threads equal to the number of mobile processors.

```kt
flexWebView.setCoroutineContext(mCoroutineContext)
flexWebView.setInterfaceThreadCount(threadCnt)
```

# WebPage

## $flex Object

\$flex Object is an object composed of interfaces between FlexWebView and Promise.  
\$flex is declared in the webpage at runtime when the webpage is loaded in the webview.  
When \$flex is finished loading, you can check the window.onFlexLoad function.  
\$flex can also be used in any accessible frames. (Ex) iframe that does not violate Cross-Origin)  
The components of $ flex Object are as follows.

```js
window.onFlexLoad; // $flex is called upon completion of loading.
$flex; // Object that contains functions that can call Native area as WebToNative
$flex.version; // get Library version
$flex.web; // Object used to add and use functions to be used for NativeToWeb
$flex.device; // Current Device Info
$flex.isAndroid; // true
$flex.isiOS; // false
```

# ToDo

Rewriting ReadMe

and Flutter version
