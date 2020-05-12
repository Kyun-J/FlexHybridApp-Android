# FlexibleHybrid

FlexibleHybridApp은 Web, Native 상호간의 Interface을 Promise로 구현하는 등, HybridApp을 개발하기 위해 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법

**minSdkVersion 19**

1. JCenter 사용  

모듈의 build.gradle에 다음을 추가.
```gradle
dependencies {
    implementation 'app.dvkyun.flexhybridand:flexhybridand:0.2'
}
```
2. jitpack 사용  

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
        implementation 'com.github.Kyun-J:FlexHybridApp-Android:0.2'
}
```

# Flex 라이브러리 인터페이스 주요 특징
기본적으로 Android의 JavascriptInterface의 단점을 보완하며 추가 제약이 있습니다.
1. Web에서 Native 함수 호출시, **Native함수의 Return이 Web에 Promise로** 전달됩니다.
2. Native에서 Web함수 호출시, **Web에서 Native로 Async**하게 반환값을 전달 할 수 있습니다.
3. Annotation외에 **Kotlin의 lambda(Java의 Interface)를 인자로 받는 함수**를 호출하여 인터페이스를 추가할 수 있습니다.
4. 기본 자료형 외에 **JS의 Array를 JAVA의(JSONArray, Array, List)으로, JS의 Object를 JAVA의(JSONObject, Map)으로** 전달할 수 있습니다.
5. Web에서 Native 호출시, **Native 코드 블럭은 CoroutineScope(Dispatchers.Default)** 안에서 동작하며 JavascriptInterface의 **JavaBridgeThread보다 약 1.5 ~ 2배**정도 나은 성능을 가집니다.
6. FlexWebView에 BaseUrl을 지정하여, **타 사이트 및 페이지에서 Native와 Interface하는 것을 방지**할 수 있습니다.
7. FlexWebView에 페이지가 최초로 로드되어 화면에 나타난 후에는 WebToNative 인터페이스를 추가 할 수 없습니다.

# Flex 인터페이스 구현
## 전달 가능한 데이터 타입
1. Android JavascriptInterface와 같이 일반 자료형 및 문자열 전송 가능합니다. 
2. **JS의 Array를 JAVA의(JSONArray, Array, List)으로, JS의 Object를 JAVA의(JSONObject, Map)으로** 전송 가능합니다.  
3. Array와 Object형식의 데이터를 전송할 때 안에 포함된 데이터는 **반드시 Null, undefined를 제외한 아래 자료형 중 하나여야 합니다**.
| JS | Kotlin(Java) |
|:--:|:--:|
| Number | Int, Long, Float, Double |
| String | String, Character | 
| Array [] | JSONArray, Array\<Any>, Iterable\<Any> |
| Object {} | JSONObject, Map\<String,Any> |
| undefined (Single Argument Only) | Null (Single Property Only) |

## WebToNative 인터페이스
WebToNative 인터페이스는 다음의 특징을 지닙니다.
1. 함수 return으로 값을 전달하는 Normal Interface, Method 호출로 값을 전달하는 Action Interface 2가지 종류
2. lambda및 Annotation function형태로 인터페이스 추가
3. Native 코드 블럭은 별도의 Background Scope에서 동작
4. 추가된 인터페이스는 Web에서 $flex.함수명 형태로 호출 가능
5. $flex Object는 window.onFlexLoad가 호출된 이후 사용 가능

### ***Nomal Interface***
Normal Interface는 기본적으로 다음과 같이 사용합니다.
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
`setInterface`의 첫 인자로 웹에서의 함수 이름을 지정하고 이어지는 lambda는 함수가 동작하는 코드 블럭이 됩니다.  
lambda로 전달되는 arguments는 JSONArray 객체로서 web에서 함수 호출시 전달된 값들이 담겨 있습니다.  
lambda에서 web으로 값을 전달할 때(return할 때)는 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다.

### ***Action Interface***
Action Interface는 Normal Interface와 거의 비슷하나, Web으로의 값 리턴을 action객체의 `promiseReturn` 메소드를 호출하는 시점에 전달합니다.
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
`promiseReturn`의 파라미터는 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다.  
`promiseReturn`메소드가 호출되지 못하면, web에서 해당 함수는 계속 pending된 상태가 되기 때문에 Action Interface를 사용시 `promiseReturn`를 반드시 호출할 수 있도록 주의가 필요합니다.  
또한 이미 `promiseReturn`가 호출되었던 FlexAction 객체는 `promiseReturn` 재 호출시 Exception이 발생하므로 2번 이상 호출하지 않도록 해야합니다.

### ***Annotation 인터페이스***
Android의 `@JavascriptInterface` 와 유사하게, Annotation을 통해 Interface 혹은 Action을 등록할 수 있습니다.
#### @FlexFunInterface
`@FlexFunInterface`는 다음 사항을 준수해야 합니다.  
1. 파라미터는 JSONArray 단 1가지만 사용 가능합니다.(다른 파라미터 추가시 Exception 발생)
2. return은 [전달 가능한 데이터 타입](#전달-가능한-데이터-타입)만 사용 가능합니다. (다른 값 리턴시 Exception 발생)
3. @FlexFunInterface가 포함된 Class를 FlexWebView.addFlexInterface에 인자로 전달해야 인터페이스가 추가됩니다.
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
NativeToWeb 인터페이스는 다음의 특징을 지닙니다.
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
window.onFlexLoad // $flex is called upon completion of loading.
$flex // Object that contains functions that can call Native area as WebToNative
$flex.version // get Library version
$flex.web // Object used to add and use functions to be used for NativeToWeb
```
상세한 사용법은 [Flex 인터페이스 구현](#Flex-인터페이스-구현) 항목을 참고하세요.
