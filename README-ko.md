# FlexibleHybrid

FlexibleHybridApp은 Web, Native 상호간의 Interface을 Promise로 구현하는 등, HybridApp을 개발하기 위해 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법

**minSdkVersion 19**  
**Minimum ChromeVersion 55**

1. jitpack 사용  

프로젝트 build.gradle에 다음을 추가
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
그후 모듈의 build.gradle에 다음을 추가
```gradle
dependencies {
        implementation 'com.github.Kyun-J:FlexHybridApp-Android:latest-version'
}
```

# Flex 라이브러리 인터페이스 주요 특징
기본적으로 Android의 JavascriptInterface의 단점을 보완하며 추가 제약이 있습니다.
1. Web에서 Native 함수 호출시, **Native함수의 Return이 Web에 Promise로** 전달됩니다.
2. Native에서 Web함수 호출시, **Web에서 Native로 Async**하게 반환값을 전달 할 수 있습니다.
3. Annotation외에 **Kotlin의 lambda(Java의 Interface)를 인자로 받는 함수**를 호출하여 인터페이스를 추가할 수 있습니다.
4. 기본 자료형 외에 **JS의 Array를 JAVA의(Array, List)으로, JS의 Object를 JAVA의 Map으로** 전달할 수 있습니다.
5. Web에서 Native 호출시, **Native 코드 블럭은 Custom Coroutine** 안에서 동작하며 JavascriptInterface의 JavaBridge Thread와 다르게 Multi Thread 로 동작하므로, 동시에 여러 인터페이스가 호출됬을 때 병렬로 처리됩니다.
6. FlexWebView에 BaseUrl을 지정하여, **타 사이트 및 페이지에서 Native와 Interface하는 것을 방지**할 수 있습니다.
7. FlexWebView에 페이지가 최초로 로드되어 화면에 나타난 후에는 WebToNative 인터페이스를 추가 할 수 없습니다.

# Flex 인터페이스 구현
## 전달 가능한 데이터 타입
1. Android JavascriptInterface와 같이 일반 자료형 및 문자열 전송 가능합니다. 
2. **JS의 Array를 JAVA의(Array, List)으로, JS의 Object를 JAVA의 Map으로** 전송 가능합니다.  
3. Array와 Object형식의 데이터를 전송할 때 안에 포함된 데이터는 **반드시 아래 자료형 중 하나여야 합니다**.  

| JS | Kotlin(Java) |
|:--:|:--:|
| Number | Int, Long, Float, Double |
| String | String | 
| Boolean | Boolean | 
| Array [] | Array, Iterable |
| Object {} | Map |
| undefined (Single Argument Only), null | null |
| Error | BrowserException |


## FlexData
Web에서 Native로 전달되는 모든 데이터는, `FlexData` 클래스로 변환되어 전달됩니다.  
`FlexData` 클래스는 Web의 데이터를 Type-Safe하게 사용하도록 도와줍니다.
```js
// in web javascript
...
const res = await $flex.CallNative("Hi Android", 100.2,[false, true]]);
// res is "HiFlexWeb"
```
```kt
// in kotlin
flexWebView.stringInterface("CallNative") // "CallNative" becomes the function name in Web JavaScript. 
{ arguments ->
    // arguments is Arguemnts Data from web. Type is Array<FlexData>
    val hello = arguments[0].asString() // hello = "Hi Android"
    val number: Float = arguments[1].reified() // number = 100.2
    val array: Array<FlexData> = arguments[2].reified() // array = [FlexData(false), FlexData(true)]
    return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
`FlexData`는 기본적으로 아래의 타입 변환 함수를 제공합니다.
```kt
fun asString(): String?
fun asInt(): Int?
fun asLong(): Long?
fun asDouble(): Double?
fun asFloat(): Float?
fun asBoolean(): Boolean?
fun asArray(): Array<FlexData>?
fun asMap(): Map<String, FlexData>?
fun asErr(): BrowserException?
```
또한, `reified` 함수를 통해 명시된 Type으로 자동 형변환하여 데이터를 가져올 수 있습니다.
```kt
inline fun <reified T> reified() : T?
```
이때 사용 가능한 데이터 타입은 `String, Int, Long, Float, Double, Boolean, Array<FlexData>, Map<String,FlexData>, BrowserException` 입니다.  
이 외의 데이터 타입을 변환하면 Exception이 발생합니다.  


## WebToNative 인터페이스
WebToNative 인터페이스는 다음의 특징을 지닙니다.
1. 함수 return으로 값을 전달하는 Normal Interface, Method 호출로 값을 전달하는 Action Interface 2가지 종류
2. lambda및 Annotation function형태로 인터페이스 추가
3. Native 코드 블럭은 별도의 Background Scope에서 동작
4. 추가된 인터페이스는 Web에서 $flex.함수명 형태로 호출 가능
5. $flex Object는 window.onFlexLoad가 호출된 이후 사용 가능

### ***Nomal Interface***
Normal Interface는 기본적으로 다음과 같이 사용합니다.
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
`stringInterface`의 첫 인자로 웹에서의 함수 이름을 지정하고 이어지는 lambda는 함수가 동작하는 코드 블럭이 됩니다.  
lambda로 전달되는 arguments는 Array<FlexData> 객체로서 web에서 함수 호출시 전달된 값들이 담겨 있습니다.  
Normal Interface의 종류는 web에 리턴하는 타입에 따라 나뉘어져 있으며, 그 종류는 다음과 같습니다.  
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
```

### ***Action Interface***
Action Interface는 Normal Interface와 거의 비슷하나, Web으로의 값 리턴을 action객체의 `promiseReturn` 메소드를 호출하는 시점에 전달합니다.
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
`promiseReturn`의 파라미터는 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다.  
`promiseReturn`메소드가 호출되지 못하면, web에서 해당 함수는 계속 pending된 상태가 되기 때문에 Action Interface를 사용시 `promiseReturn`를 반드시 호출할 수 있도록 주의가 필요합니다.  
또한 이미 `promiseReturn`가 호출되었던 FlexAction 객체는 `promiseReturn`을 중복 호출하여도 아무런 일도 일어나지 않습니다.

### ***Annotation 인터페이스***
Android의 `@JavascriptInterface` 와 유사하게, Annotation을 통해 Interface 혹은 Action을 등록할 수 있습니다.
#### @FlexFunInterface
`@FlexFunInterface`는 다음 사항을 준수해야 합니다.  
1. 파라미터는 Array<FlexData> 단 1가지만 사용 가능합니다.(다른 파라미터 추가시 Exception 발생)
2. return은 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다. (다른 값 리턴시 Exception 발생)
3. @FlexFunInterface가 포함된 Class를 FlexWebView.addFlexInterface에 인자로 전달해야 인터페이스가 추가됩니다.
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
`@FlexActionInterface`는 다음 사항을 준수해야 합니다.  
1. 파라미터는 **FlexAction, Array<FlexData>의 순서대로** 선언해야 하며, 다른 파라미터는 사용할 수 없습니다.(위반시 Exception 발생)
2. return은 선언 가능하나, **사용되지 않습니다**.
3. Web에 리턴값 전송시, 전달된 FlexAction 파라미터의 `promiseReturn`을 사용해야 합니다.
4. `promiseReturn`의 파라미터는 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다.
5. `promiseReturn`메소드가 호출되지 못하면, web에서 해당 함수는 계속 pending된 상태가 되기 때문에 Action Interface를 사용시 `promiseReturn`를 반드시 호출할 수 있도록 주의가 필요합니다.  
6. `promiseReturn`은 1번만 동작하며, 중복 호출 시 아무 일도 일어나지 않습니다.
7. @FlexActionInterface가 포함된 Class를 FlexWebView.addFlexInterface에 인자로 전달해야 인터페이스가 추가됩니다.
```kt
class MyInterface {
    @FlexActionInterface
    fun actionInterface(action: FlexAction, arguments: Array<FlexData>) {
        // .... work something
        action.promiseReturn(1)
    }
}
...
// in activity
mFlexWebView.addFlexInterface(MyInterface())
```
```js
...
const res = await $flex.actionInterface();
// res is 1
```

### ***class FlexInterfaces***
FlexWebView의 Interface 추가 기능만 분리한 별도의 Class 입니다.  
FlexWebView에 직접 Interface를 추가하지 않고, FlexInterfaces에 추가한 후 `FlexWebView.addFlexInterface`로 전달하면 FlexWebView에 인터페이스가 추가됩니다.
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
`Interface`코드 블럭내에서 Exception이 발생하면 Web에 오류 발생 사항이 전달됩니다.
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
`FlexAction`에서는, `promiseReturn`에 `BrowserException`객체를 보내거나, `reject`함수를 호출하여 손쉽게 에러사항을 전달할 수 있습니다.  
```kt
// in kotlin
flexComponent.setAction("errorAction")
{ action, arguments ->
    action.reject("errorAction") // = action.promiseReturn(BrowserException("errorAction"))
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

## NativeToWeb 인터페이스
NativeToWeb 인터페이스는 다음의 특징을 지닙니다.
1. Web의 $flex.web Object 안에 함수를 추가하면, Native(FlexWebView)에서 `evalFlexFunc` 메소드를 통해 해당 함수를 호출할 수 있습니다.
2. window.onFlexLoad 호출 후($flex 생성 후) $flex.web에 함수 추가가 가능합니다.
3. $flex.web 함수는, 일반 return 및 Promise return을 통해 Native에 값을 전달 할 수 있습니다.

```js
// in js
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
// in kotlin
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
if(BuildConfig.DEBUG) setWebContentsDebuggingEnabled(true)
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
설정한 BaseUrl이 포함된 Page에서만 $flex Object 사용이 가능합니다.  
한번 설정한 BaseUrl은 다시 수정할 수 없습니다.
```kt
fun setBaseUrl(url: String)
fun getBaseUrl(): String?
```

### InterfaceTimeout
Interface가 실행된 후, return이 발생할 때 까지 기다리는 시간을 설정합니다.  
해당 시간이 지나면, 인터페이스로 생성된 Promise는 강제 Exception 처리됩니다.  
```kt
fun setInterfaceTimeout(timeout: Int)
```

### InterfaceThreadCount
FlexInterface가 실행되는 Thread 개수를 설정합니다.  
기본값은 cpu 코어 갯수(=Runtime.getRuntime().availableProcessors()) 입니다.
```kt
fun setInterfaceThreadCount(count: Int)
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
사용 가능한 메소드는 아래와 같으며, promiseReturn 함수만 Web으로 return값을 전달하는 역할을 합니다.  
resolveVoid는 nil 값을 전달하며(promiseReturn(nil)과 동일)  
reject함수는 BrowserException 객체를 자동으로 생성하여 전달합니다.(promiseReturn(BrowserException)와 동일)
```kt
fun promiseReturn(...) // Transferable-Data-Type
fun resolveVoid()
fun reject(reason: String)
fun reject(reason: BrowserException)
fun reject()
```
위 함수중 하나라도 호출했다면, 다음에 어떤 함수를 호출하더라도 Web에 값이 전달되지 않습니다.
FlexAction Class를 직접 생성 및 사용하면 아무런 효과도 얻을 수 없으며, 오직 인터페이스상에서 생성되어 전달되는 FlexAction만이 효력을 가집니다.

## FlexInterfaces
FlexInterfaces 클래스는 FlexWebView에서 setInterface, setAction 기능만 따로 분리한 클래스 입니다.
사용 예제는 [인터페이스 예제](#class-FlexInterfaces) 항목을 참고하세요
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
\$flex Object는 FlexWebView를 와 Promise 형태로 상호간 인터페이스가 구성되어있는 객체입니다.  
$flex Object의 구성 요소는 다음과 같습니다.
```js
window.onFlexLoad // $flex is called upon completion of loading.
$flex // Object that contains functions that can call Native area as WebToNative
$flex.version // get Library version
$flex.web // Object used to add and use functions to be used for NativeToWeb
$flex.device // Current Device Info
$flex.isAndroid // true
$flex.isiOS // false
```
상세한 사용법은 [Flex 인터페이스 구현](#Flex-인터페이스-구현) 항목을 참고하세요.