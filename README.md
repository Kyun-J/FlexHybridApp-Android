
[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

FlexibleHybridApp is a library that provides various convenience functions to develop HybridApp, such as implementing interface between WebPage and Native with promises.

# How to add libraries

**minSdkVersion 19**  
**Minimum ChromeVersion 55**

1. Enable jitpack

Add the following to the project build.gradle
```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Then add the following to the build.gradle of the module:
```Gradle
dependencies {
    implementation 'com.github.Kyun-J:FlexHybridApp-Android:latest-version'
}
```

# Interface Key Features of Flex Library
Basically, it compensates for the shortcomings of Android's JavascriptInterface and has additional restrictions.
1. When a native function is called from the Web, **the return of the native function is passed to Promise** on the Web.
2. When calling the Web function from Native, the return value **can be passed Async** from Web to Native.
3. In addition to annotations, **you can add an interface by invoking a function that receives the Lambda (interface of Java) of Kotlin as a factor**.
4. In addition to the basic data type, **JS array can be delivered as (Array, List) of Kotlin(JAVA) and JS Object as (Map) of Kotlin(JAVA)**.
5. When calling Native on the web, **Native code block operates in Custom Coroutine** and operates as Multi Thread unlike JavaBridge Thread of JavascriptInterface, so it is processed in parallel when multiple interfaces are called at the same time.
6. By specifying BaseUrl in FlexWebView, you can **prevent native and interface on other sites and pages**.
7. You cannot add an interface after the page is first loaded into FlexWebView and appears on the screen.

# Flex Interface implementation
## Transferable Data Type
1. It is possible to transfer general data types and strings, such as Android JavascriptInterface.
2. It is possible to transfer **JS Array to Kotlin(JAVA) (Array, List) and JS Object to Kotlin(JAVA) (Map)**.
3. When transferring data of type Array and Object, the data contained in it **must be one of the following data types**.
   
| JS | Kotlin(Java) |
|:--:|:--:|
| Number | Int, Long, Float, Double |
| String | String, Char | 
| Boolean | Boolean | 
| Array [] | Array, Iterable |
| Object {} |  Map |
| undefined (Single Argument Only), null | Null |
| Error | FlexReject |

## FlexData
All data delivered from Web to Native is converted into `FlexData` class and delivered.  
The'FlexData' class helps you use Web data in a type-safe way.  
```js
// in web javascript
...
const res = await $flex.CallNative("Hi Android", 100.2,[false, true]]);
// res is "HiFlexWeb"
```
```kt
flexWebView.stringInterface("CallNative") // "CallNative" becomes the function name in Web JavaScript. 
{ arguments ->
    // arguments is Arguemnts Data from web. Type is Array<FlexData>
    val hello = arguments[0].asString() // hello = "Hi Android"
    val number: Float = arguments[1].reified() // number = 100.2
    val array: Array<FlexData> = arguments[2].reified() // array = [FlexData(false), FlexData(true)]
    return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
`FlexData` basically provides the following type conversion functions.  
```kt
fun asString(): string?
fun asInt (): Int?
fun asLong (): long?
fun asDouble (): Double?
fun asFloat (): Float?
fun asBoolean(): Boolean?
fun asArray(): Array <FlexData>?
fun asMap(): Map <String, FlexData>?
fun asErr(): BrowserException?
```
You can also use the data by automatically casting it to an advanced type through the `reified` function.  
```kt
inline fun <reified T> reified (): T?
```
Available data types are `String, Int, Long, Float, Double, Boolean, Array<FlexData>, Map<String,FlexData>, BrowserException`.  
When converting other data types, an exception occurs.

## WebToNative Interface
The WebToNative interface has the following features.
1. Two types of normal interface, which passes values by function return, and action interface, which passes values by method call
2. Add interface in the form of lambda and annotation function
3. Native code blocks operate in a separate Background Scope
4. The added interface can be called in the form of $flex.function on the web.
5. $flex Object can be used after window.onFlexLoad is called

### ***Normal Interface***
Normal Interface is basically used as follows.
```js
// in web javascript
...
const res = await $flex.Normal("data1",2,false);
// res is "HiFlexWeb"
```
```kt
// in Kotlin
flexWebView.stringInterface("Normal") // "Normal" becomes the function name in Web JavaScript. 
{ arguments ->
    // arguments is Arguemnts Data from web. Type is Array<FlexData>
    // ["data", 2, false]
    return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
Specify the function name on the web as the first argument of `stringInterface`, and the following lambda becomes a block of code where the function operates.  
The arguments passed to lambda are Array<FlexData> objects, which contain the values passed when calling the function on the web.  
The types of Normal Interface are divided according to the type returned to the web, and the types are as follows.
```kt
fun voidInterface(name: String, lambda: (Array<FlexData>) -> Unit): FlexWebView
fun stringInterface(name: String, lambda: (Array<FlexData>) -> String): FlexWebView
fun intInterface(name: String, lambda: (Array<FlexData>) -> Int): FlexWebView 
fun charInterface(name: String, lambda: (Array<FlexData>) -> Char): FlexWebView
fun longInterface(name: String, lambda: (Array<FlexData>) -> Long): FlexWebView
fun doubleInterface(name: String, lambda: (Array<FlexData>) -> Double): FlexWebView
fun floatInterface(name: String, lambda: (Array<FlexData>) -> Float): FlexWebView
fun boolInterface(name: String, lambda: (Array<FlexData>) -> Boolean): FlexWebView
fun arrayInterface(name: String, lambda: (Array<FlexData>) -> Array<*>): FlexWebView
fun mapInterface(name: String, lambda: (Array<FlexData>) -> Map<String, *>): FlexWebView
```

### ***Action Interface***
The Action Interface is almost the same as the Normal Interface, but it returns the value return to the Web at the time of calling the `promiseReturn` method of the action object.
```kt
// in Kotlin
var mAction: FlexAction? = null
...
flexWebView.setAction("Action")
{ action, arguments ->
// arguments is Array<FlexData>, ["Who Are You?"]
// action is FlexAction Object
    mAction = action
}
...
// Returns to the Web when calling promiseReturn.
mAction.promiseReturn(arrayOf("FlexAction!!!",100));
mAction = null
```
```js
// in web javascript
....
const res = await $flex.Action("Who Are You?"); // Pending until promiseReturn is called...
// res is ["FlexAction!!!", 100]
```
The parameter of `promiseReturn` is only available for [Transferable Data Type](#Transferable-Data-Type).  
If the `promiseReturn` method is not called, the function in the web will be in a pending state, so be careful to call` promiseReturn` when using the Action Interface.  
In addition, FlexAction objects that have already called `promiseReturn` do not happen even if duplicate`promiseReturn` is called.

### ***Annotation Interface***
Similar to Android's `@JavascriptInterface`, Interface or Action can be registered through Annotation.
#### @FlexFunInterface
`@FlexFunInterface` must comply with the following:
1. Only one Array<FlexData> parameter can be used. (Exception occurs when adding another parameter)
2. Return can only use [Transferable Data Type](#Transferable-Data-Type). (Exception occurs when returning another value)
3. The class containing @FlexFunInterface must be passed as an argument to FlexWebView.addFlexInterface to add an interface.
```kt
class MyInterface {
    @FlexFunInterface
    fun funInterface(arguments: Array<FlexData>): Int {
        // .... work something
        return 1
    }
}
...
// in activity
mFlexWebView.addFlexInterface(MyInterface())
```
```js
...
const res = await $flex.funInterface();
// res is 1
```

#### @FlexActionInterface
`@FlexActionInterface` must comply with the following:
1. Parameters must be declared in **the order of FlexAction, Array<FlexData>**, and other parameters cannot be used. (Exception occurs when violation occurs)
2. Return can be declared, but not used.
3. When sending the return value to the web, you should use the `promiseReturn` of the passed FlexAction parameter.
4. The parameters of `promiseReturn` are only available for [Transferable Data Type](#Transferable-Data-Type).
5. If the `promiseReturn` method cannot be called, the function on the web will be in a pending state, so be careful to call `promiseReturn` when using the Action Interface.
6. `promiseReturn` works only once, and nothing happens when a duplicate call is made.
7. The class containing @FlexActionInterface must be passed as an argument to FlexWebView.addFlexInterface to add the interface.
```kt
class MyInterface {
    @FlexActionInterface
    fun actionInterface(action: FlexAction, arguments: Array<FlexData>) {
        // .... work something
        action.promiseReturn(Array<FlexData>())
    }
}
...
// in activity
mFlexWebView.addFlexInterface(MyInterface())
```
```js
...
const res = await $flex.actionInterface();
// res is []
```

### ***class FlexInterfaces***
It is a separate class that separates only the interface addition function of FlexWebView.  
Instead of adding an interface directly to FlexWebView, add it to FlexInterfaces and pass it to `FlexWebView.addFlexInterface` to add the interface to FlexWebView.
```java
// in java code
public class FlexInterfaceExample extends FlexInterfaces {
    FlexInterfaceExample() {
        this.voidInterface("test1", new Function2<FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexData[] arguments, Continuation<? super Unit> continuation) {
               return null;
            }
        }).setAction("test2", new Function3<FlexAction, FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexAction flexAction, FlexData[] flexData, Continuation<? super Unit> continuation) {
                ...
                return null;
            }
        }).voidInterface("test3", new Function2<FlexData[], Continuation<? super Unit>, Object>() {
            @Override
            public Object invoke(FlexData[] arguments, Continuation<? super Unit> continuation {
                ...
                return null;
            }
        });
    }

    @FlexFuncInterface
    public void test4(FlexData[] arguments) {
        ...
    }

    @FlexActionInterface
    public void test5(FlexAction action, FlexData[] arguments) {
        action.promiseReturn(null);
    }
}
```
```kt
// in activity...
...
// add interface test1, test2, test3, test4, test5
mFlexWebView.addFlexInterface(FlexInterfaceExample())
let other = FlexInterfaces()
other.voidInterface("test6")
{ arguments ->

}
other.setAction("test7")
{ action, arguments ->
    action.promiseReturn(null)
}
// add interface test6, test7
mFlexWebView.addFlexInterface(other)
```

### ***Error Interface***
If an exception occurs within the `Interface` code block, the error occurrence is delivered to the Web.
```kt
// in kotlin
mFlexWebView.voidInterface("errorTest")
{ arguments -> 
    throw Exception("errorTest")   
}
```
```js
// in js
...
try {
    const result = await $flex.errorTest();
} catch(e) {
    // e is Error("errorTest")
}
```
In `FlexAction`, you can send a `BrowserException` object to `promiseReturn` or call `reject` function to easily deliver the error details.
```kt
// in kotlin
flexComponent.setAction("errorAction")
{ action, arguments ->
    action.reject("errorAction") // = action.promiseReturn(FlexReject("errorAction"))
}
```
```js
// in js
...
try {
    const result = await $flex.errorAction();
} catch(e) {
    // e is Error("errorAction")
}
```

## NativeToWeb Interface
The NativeToWeb interface has the following features.
1. If you add a function in the web's $flex.web Object, you can call the function through the `evalFlexFunc` method in Native(FlexWebView).
2. After calling `window.onFlexLoad` (after creating $flex), you can add a function to $flex.web.
3. The $flex.web function can pass values ​​to Native through regular return and promise return.

```js
window.onFlexLoad = () => {
    $flex.web.webFunc = (data) => {
        // data is ["data1","data2"]
        return data[0]; // "data1"
    }
    $flex.web.promiseReturn = () => {
        return Promise.resolve("this is promise")
    }
}
```
```kt
...
// call function, send data, get response
mFlexWebView.evalFlexFunc("webFunc",arrayOf("data1","data2")) // same as $flex.web.webFunc(["data1","data2"])
{ res ->
    // res is "data1"
}
mFlexWebView.evalFlexFunc("promiseReturn") // same as $flex.web.promiseReturn()
{ res ->
    // res is "this is promise"
}
// just call function
mFlexWebView.evalFlexFunc("promiseReturn")
// call function and send data
mFlexWebView.evalFlexFunc("webFunc",arrayOf("data1","data2"))
```

# Native Class 
Describes native classes of libraries including FlexWebView.

## FlexWebView
FlexWebView has the following features.
1. It was created by inheriting Android WebView and is very similar to WebView.
2. WebViewClient, WebChromeClient should use FlexWebViewClient, FlexWebChromeClient class. (If not used, exception occurs)
3. You can use the existing Android JavascriptInterface. (In this case, you cannot use the Promise pattern interface using $flex.)
4. BaseUrl must be specified, and $flex Object in WebPage can be used only in the URL containing the corresponding BaseUrl.
5. Default of WebViewSettings is different from existing WebView.

### WebViewSettings
FlexWebView defaults to the settings below.  
This setting is applied when FlexWebView is declared and can be changed at any time.
```kt
settings.javaScriptEnabled = true
settings.displayZoomControls = false
settings.builtInZoomControls = false
settings.setSupportZoom(false)
settings.textZoom = 100
settings.domStorageEnabled = true
settings.loadWithOverviewMode = true
settings.loadsImagesAutomatically = true
settings.useWideViewPort = true
settings.cacheMode = WebSettings.LOAD_DEFAULT
settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
settings.mediaPlaybackRequiresUserGesture = false
settings.enableSmoothTransition()
settings.javaScriptCanOpenWindowsAutomatically = true
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
}
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
    setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, true)
}
```

### BaseUrl
\$Flex Object can be used only in the page containing the configured BaseUrl.  
Once set, the BaseUrl cannot be modified again.
```kt
fun setBaseUrl(url: String)
fun getBaseUrl(): String?
```

### InterfaceTimeout
Set the time to wait for return after Interface is executed.  
After that time, the Promise created by the interface is forcibly rejected.
```kt
fun setInterfaceTimeout(timeout: Int)
```

### InterfaceThreadCount
Set the number of threads when Interface is executed.  
The default is the number of cpu cores(=Runtime.getRuntime().availableProcessors()).
```kt
fun setInterfaceThreadCount(count: Int)
```

### FlexWebViewClient, FlexWebChromeClient
FlexWebView must use FlexWebViewClient and FlexWebChromeClient.  
In the setter, WebChromeClient and WebViewClient are received as arguments, but an exception occurs if the object cannot be Cased with FlexWebViewClient or FlexWebChromeClient.  
FlexWebChromeClient includes code that enables WebView full screen, so you can switch to full screen by calling requestFullscreen of js.
```kt
fun getWebChromeClient(): FlexWebChromeClient
fun setWebChromeClient(client: WebChromeClient)
fun getWebViewClient(): FlexWebViewClient
fun setWebViewClient(client: WebViewClient)
```

### WebToNative Interface Setting
Add an interface to the FlexWebView.
For details, refer to [WebToNavite Interface](#WebToNative-Interface).
```kt
fun voidInterface(name: String, lambda: suspend (Array<FlexData>) -> Unit): FlexWebView
fun stringInterface(name: String, lambda: suspend (Array<FlexData>) -> String): FlexWebView
fun intInterface(name: String, lambda: suspend (Array<FlexData>) -> Int): FlexWebView 
fun charInterface(name: String, lambda: suspend (Array<FlexData>) -> Char): FlexWebView
fun longInterface(name: String, lambda: suspend (Array<FlexData>) -> Long): FlexWebView
fun doubleInterface(name: String, lambda: suspend (Array<FlexData>) -> Double): FlexWebView
fun floatInterface(name: String, lambda: suspend (Array<FlexData>) -> Float): FlexWebView
fun boolInterface(name: String, lambda: suspend (Array<FlexData>) -> Boolean): FlexWebView
fun arrayInterface(name: String, lambda: suspend (Array<FlexData>) -> Array<*>): FlexWebView
fun mapInterface(name: String, lambda: suspend (Array<FlexData>) -> Map<String, *>): FlexWebView
fun setAction(name: String, action: suspend (action: FlexAction?, arguments: Array<FlexData>) -> Unit): FlexWebView
fun addFlexInterface(flexInterfaces: Any) 
```

### NativeToWeb Interface Setting
Implement the interface by calling the function added to $ flex.
For details, refer to [NativeToWeb Interface](#NativeToWeb-Interface).
```kt
fun evalFlexFunc(funcName: String)
fun evalFlexFunc(funcName: String, response: (Array<FlexData>) -> Unit)
fun evalFlexFunc(funcName: String, sendData: Any?)
fun evalFlexFunc(funcName: String, sendData: Any?, response: (Array<FlexData>) -> Unit)
```

## FlexAction
Created when WebToNative interface added with setAction, @FlexActionInterface is called.  
The reject function automatically creates and passes a FlexReject object (same as promiseReturn(FlexReject)).
```kt
fun promiseReturn(...) // Transferable-Data-Type
fun resolveVoid()
fun reject(reason: String)
fun reject(reason: BrowserException)
fun reject()
```
If any of the above functions is called, the next time any function is called, the value is not passed to the Web.  
If you directly create and use FlexAction Class, there is no effect. Only FlexAction created and delivered on the interface is effective.

## FlexInterfaces
FlexInterfaces class is a class that separates only setInterface and setAction functions from FlexWebView.  
Refer to [Interface example](#class-FlexInterfaces) for usage examples.
```kt
fun voidInterface(name: String, lambda: suspend (Array<FlexData>) -> Unit): FlexInterfaces
fun stringInterface(name: String, lambda: suspend (Array<FlexData>) -> String): FlexInterfaces
fun intInterface(name: String, lambda: suspend (Array<FlexData>) -> Int): FlexInterfaces 
fun charInterface(name: String, lambda: suspend (Array<FlexData>) -> Char): FlexInterfaces
fun longInterface(name: String, lambda: suspend (Array<FlexData>) -> Long): FlexInterfaces
fun doubleInterface(name: String, lambda: suspend (Array<FlexData>) -> Double): FlexInterfaces
fun floatInterface(name: String, lambda: suspend (Array<FlexData>) -> Float): FlexInterfaces
fun boolInterface(name: String, lambda: suspend (Array<FlexData>) -> Boolean): FlexInterfaces
fun arrayInterface(name: String, lambda: suspend (Array<FlexData>) -> Array<*>): FlexInterfaces
fun mapInterface(name: String, lambda: suspend (Array<FlexData>) -> Map<String, *>): FlexInterfaces
fun setAction(name: String, action: suspend (action: FlexAction?, arguments: Array<FlexData>) -> Unit): FlexInterfaces
```

# $flex Object
\$flex Object is an object composed of interfaces between FlexWebView and Promise.  
The components of $ flex Object are as follows.
```js
window.onFlexLoad // $flex is called upon completion of loading.
$flex // Object that contains functions that can call Native area as WebToNative
$flex.version // get Library version
$flex.web // Object used to add and use functions to be used for NativeToWeb
$flex.device // Current Device Info
$flex.isAndroid // true
$flex.isiOS // false
```
For detailed usage, refer to [Flex Interface Implementation](#Flex-Interface-Implementation).
