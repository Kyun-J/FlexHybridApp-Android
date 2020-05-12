
[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

FlexibleHybridApp is a library that provides various convenience functions to develop HybridApp, such as implementing interface between WebPage and Native with promises.

# How to add libraries

**minSdkVersion 19**

1. Enable JCenter

Add the following to the build.gradle of the module.
```Gradle
dependencies {
    implementation 'app.dvkyun.flexhybrid:flexhybrid:0.2'
}
```
2. Enable jitpack

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
    implementation 'com.github.Kyun-J:FlexHybridApp-Android:0.2'
}
```

# Interface Key Features of Flex Library
Basically, it compensates for the shortcomings of Android's JavascriptInterface and has additional restrictions.
1. When a native function is called from the Web, **the return of the native function is passed to Promise** on the Web.
2. When calling the Web function from Native, the return value **can be passed Async** from Web to Native.
3. In addition to annotations, **you can add an interface by invoking a function that receives the Lambda (interface of Java) of Kotlin as a factor**.
4. In addition to the basic data type, **JS array can be delivered as (JSONARray, Array, List) of Kotlin(JAVA) and JS Object as (JSONObject, Map) of Kotlin(JAVA)**.
5. When calling Native from the Web, **Native code blocks operate within CoroutineScope (Dispatchers.Default) and perform about 1.5 to 2 times better than JavaBridgeThread** in JavascriptInterface.
6. By specifying BaseUrl in FlexWebView, you can **prevent native and interface on other sites and pages**.
7. You cannot add an interface after the page is first loaded into FlexWebView and appears on the screen.

# Flex Interface implementation
## Transferable Data Type
1. It is possible to transfer general data types and strings, such as Android JavascriptInterface.
2. It is possible to transfer **JS Array to Kotlin(JAVA) (JSONArray, Array, List) and JS Object to Kotlin(JAVA) (JSONObject, Map)**.
3. When transferring data of type Array and Object, the data contained in it **must be one of the following data types except Null and undefined**.
   
| JS | Kotlin(Java) |
|:--:|:--:|
| Number | Int, Long, Float, Double |
| String | String, Character | 
| Array [] | JSONArray, Array\<Any>, Iterable\<Any> |
| Object {} | JSONObject, Map\<String,Any> |
| undefined (Single Argument Only) | Null (Single Property Only) |

## WebToNative Interface
The WebToNative interface has the following features.
1. Two types of normal interface, which passes values by function return, and action interface, which passes values by method call
2. Add interface in the form of lambda and annotation function
3. Native code blocks operate in a separate Background Scope
4. The added interface can be called in the form of $flex.function on the web.
5. $flex Object can be used after window.onFlexLoad is called

### ***Normal Interface***
Normal Interface is basically used as follows.
```kt
// in Kotlin
flexWebView.setInterface("Normal") // "Normal" becomes the function name in Web JavaScript. 
{ arguments ->
    // arguments is Arguemnts Data from web. Type is JSONArray
    // ["data", 2, false]
    return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
```js
// in web javascript
...
const res = await $flex.Normal("data1",2,false);
// res is "HiFlexWeb"
```
Specify the function name on the web as the first argument of `setInterface`, and the following lambda becomes a block of code where the function operates.  
The arguments passed to lambda are JSONArray objects, which contain the values passed when calling the function on the web.  
When passing a value from lambda to web (when returning), only [Transferable Data Type](#Transferable-Data-Type) is available.

### ***Action Interface***
The Action Interface is almost the same as the Normal Interface, but it returns the value return to the Web at the time of calling the `promiseReturn` method of the action object.
```kt
// in Kotlin
var mAction: FlexAction? = null
...
flexWebView.setAction("Action")
{ action, arguments ->
// arguments is JSONObject, ["Who Are You?"]
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
In addition, FlexAction object that had already called `promiseReturn` is an exception when calling` promiseReturn`, so you should not call it more than once.  

### ***Annotation Interface***
Similar to Android's `@JavascriptInterface`, Interface or Action can be registered through Annotation.
#### @FlexFunInterface
`@FlexFunInterface` must comply with the following:
1. Only one JSONArray parameter can be used. (Exception occurs when adding another parameter)
2. Return can only use [Transferable Data Type](#Transferable-Data-Type). (Exception occurs when returning another value)
3. The class containing @FlexFunInterface must be passed as an argument to FlexWebView.addFlexInterface to add an interface.
```kt
class MyInterface {
    @FlexFunInterface
    fun funInterface(arguments: JSONArray): JSONObject {
        // .... work something
        return JSONObject()
    }
}
...
// in activity
mFlexWebView.addFlexInterface(MyInterface())
```
```js
...
const res = await $flex.funInterface();
// res is {}
```
#### @FlexActionInterface
`@FlexActionInterface`는 다음 사항을 준수해야 합니다.  
1. 파라미터는 **FlexAction, JSONArray의 순서대로** 선언해야 하며, 다른 파라미터는 사용할 수 없습니다.(위반시 Exception 발생)
2. return은 선언 가능하나, **사용되지 않습니다**.
3. Web에 리턴값 전송시, 전달된 FlexAction 파라미터의 `promiseReturn`을 사용해야 합니다.
4. `promiseReturn`의 파라미터는 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다.
5. `promiseReturn`메소드가 호출되지 못하면, web에서 해당 함수는 계속 pending된 상태가 되기 때문에 Action Interface를 사용시 `promiseReturn`를 반드시 호출할 수 있도록 주의가 필요합니다.  
6. `promiseReturn` 재 호출시 Exception이 발생하므로 2번 이상 호출하지 않도록 해야합니다.
7. @FlexActionInterface가 포함된 Class를 FlexWebView.addFlexInterface에 인자로 전달해야 인터페이스가 추가됩니다.
```kt
class MyInterface {
    @FlexActionInterface
    fun actionInterface(action: FlexAction, arguments: JSONArray) {
        // .... work something
        action.promiseReturn(JSONArray())
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
FlexWebView의 Interface 추가 기능만 분리한 별도의 Class 입니다.  
FlexWebView에 직접 Interface를 추가하지 않고, FlexInterfaces에 추가한 후 `FlexWebView.addFlexInterface`로 전달하면 FlexWebView에 인터페이스가 추가됩니다.
```java
public class FlexInterfaceExample extends FlexInterfaces {
    FlexInterfaceExample() {
        this.setInterface("test1", new Function1<JSONArray, Object>() {
            @Override
            public Object invoke(JSONArray arguments) {
               return null;
            }
        }).setAction("test2", new Function2<FlexAction, JSONArray, Unit>() {
            @Override
            public Unit invoke(final FlexAction flexAction, JSONArray arguments) {
                ...
                return null;
            }
        }).setInterface("test3", new Function1<JSONArray, Object>() {
            @Override
            public Object invoke(JSONArray arguments) {
                ...
                return null;
            }
        });
    }

    @FlexFuncInterface
    public void test4(JSONArray arguments) {
        ...
    }

    @FlexActionInterface
    public void test5(FlexAction action, JSONArray arguments) {
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
other.setInterface("test6")
{ arguments ->
    return null
}
other.setAction("test7")
{ action, arguments ->
    action.promiseReturn(null)
}
// add interface test6, test7
mFlexWebView.addFlexInterface(other)
```

## NativeToWeb 인터페이스
WebToNative 인터페이스는 다음의 특징을 지닙니다.
1. Web의 $flex.web Object 안에 함수를 추가하면, Native(FlexWebView)에서 `evalFlexFunc` 메소드를 통해 해당 함수를 호출할 수 있습니다.
2. window.onFlexLoad 호출 후($flex 생성 후) $flex.web에 함수 추가가 가능합니다.
3. $flex.web 함수는, 일반 return 및 Promise return을 통해 Native에 값을 전달 할 수 있습니다.

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
mFlexWebView.evalFlexFunc("webFunc",arrayOf("data1","data2"))
{ res ->
    // res is "data1"
}
// just call function
mFlexWebView.evalFlexFunc("promiseReturn")
// call function and send data
mFlexWebView.evalFlexFunc("webFunc",arrayOf("data1","data2"))
```

# Native Class 
FlexWebView를 비롯한 라이브러리의 Native class를 설명합니다.
## FlexWebView
FlexWebView는 다음의 특징을 지닙니다.
1. Android WebView 상속하여 제작되었으며, WebView와 매우 유사합니다.
2. WebViewClient, WebChromeClient는 FlexWebViewClient, FlexWebChromeClient class를 사용하여야 합니다.(사용하지 않을경우, Exception 발생)
3. 기존 Android의 JavascriptInterface를 사용할 수 있습니다. (이 경우, $flex를 사용한 Promise pattern interface 사용 불가.)
4. BaseUrl을 지정하여야 하며, WebPage에서 $flex Object는 해당 BaseUrl이 포함된 Url에서만 사용할 수 있습니다.
5. WebViewSettings의 Default가 기존 WebView와 차이가 있습니다.

### WebViewSettings
FlexWebView는 아래 설정이 기본으로 설정되어 있습니다.  
이 설정은 FlexWebView가 선언될때 적용되며, 언제든 변경 가능합니다.
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
settings.enableSmoothTransition()
settings.javaScriptCanOpenWindowsAutomatically = true
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
}
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
    setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, true)
}
```
### BaseUrl 설정
설정한 BaseUrl이 포함된 Page에서만 $flex Object 사용이 가능합니다.  
한번 설정한 BaseUrl은 다시 수정할 수 없습니다.
```kt
fun setBaseUrl(url: String)
fun getBaseUrl(): String? = baseUrl
```
### FlexWebViewClient, FlexWebChromeClient
FlexWebView는 반드시 FlexWebViewClient, FlexWebChromeClient를 사용하여야 합니다.
setter에서는 WebChromeClient, WebViewClient를 인자로 받으나, 해당 객체가 FlexWebViewClient, FlexWebChromeClient로 Case 되지 못하면 Exception이 발생합니다.  
FlexWebChromeClient에는 WebView 전체화면이 가능하게 하는 코드가 포함되어 있으므로, js의 requestFullscreen를 호출하여 전체화면 전환이 가능합니다.
```kt
fun getWebChromeClient(): FlexWebChromeClient
fun setWebChromeClient(client: WebChromeClient)
fun getWebViewClient(): FlexWebViewClient
fun setWebViewClient(client: WebViewClient)
```

### WebToNative Interface Setting
FlexWebView에 인터페이스를 추가합니다.  
상세한 사항은 [WebToNavite 인터페이스](#WebToNative-인터페이스) 항목을 참고하세요.
```kt
fun setInterface(name: String, lambda: (JSONArray?) -> Any?): FlexWebView 
fun setAction(name: String, action: (action: FlexAction?, arguments: JSONArray?) -> Unit): FlexWebView
fun addFlexInterface(flexInterfaces: Class<*>) 
```

### NativeToWeb Interface Setting
$flex에 추가된 함수를 호출하여 인터페이스를 실시합니다.
상세한 사항은 [NativeToWeb 인터페이스](#NativeToWeb-인터페이스) 항목을 참고하세요.
```kt
fun evalFlexFunc(funcName: String)
fun evalFlexFunc(funcName: String, response: (Any?) -> Unit)
fun evalFlexFunc(funcName: String, sendData: Any?)
fun evalFlexFunc(funcName: String, sendData: Any?, response: (Any?) -> Unit)
```

## FlexAction
setAction, @FlexActionInterface로 추가된 WebToNative 인터페이스가 호출될 시 생성됩니다.  
사용 가능한 메소드는 promiseReturn 하나이며, Web으로 return값을 전달하는 역할을 합니다.
```kt
fun promiseReturn(response: Any?)
```
promiseReturn은 한번 호출 후에는 다시 사용할 수 없습니다.  
FlexAction Class를 직접 생성 및 사용하면 아무런 효과도 얻을 수 없으며, 오직 인터페이스상에서 생성되어 전달되는 FlexAction만이 효력을 가집니다.

## FlexInterfaces
FlexInterfaces 클래스는 FlexWebView에서 setInterface, setAction 기능만 따로 분리한 클래스 입니다.
사용 예제는 [인터페이스 예제](#class-FlexInterfaces) 항목을 참고하세요
```kt
fun setInterface(name: String, lambda: (JSONArray?) -> Any?): FlexInterfaces
fun setAction(name: String, action: (action: FlexAction?, arguments: JSONArray?) -> Unit): FlexInterfaces
```

# $flex Object
\$flex Object는 FlexWebView를 와 Promise 형태로 상호간 인터페이스가 구성되어있는 객체입니다.  
$flex Object의 구성 요소는 다음과 같습니다.
```js
$flex // Object that contains functions that can call Native area as WebToNative
$flex.version // get Library version
$flex.web // Object used to add and use functions to be used for NativeToWeb
```
상세한 사용법은 [Flex 인터페이스 구현](#Flex-인터페이스-구현) 항목을 참고하세요.
