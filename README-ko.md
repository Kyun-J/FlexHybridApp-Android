[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

Webview와 Native간 인터페이스 간소화, 비동기 처리를 비롯하여  
Webview사용에 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법

**minSdkVersion 23**  
**Minimum Android Webview Version 55**

1. jitpack 사용

gradle의 repositories에 다음을 추가

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

gradle의 dependencies 다음을 추가

```gradle
dependencies {
    ...
    implementation 'com.github.Kyun-J:FlexHybridApp-Android:latest-version'
}
```

# Flex 라이브러리 주요 특징

1. [iOS 버전](https://github.com/Kyun-J/FlexHybridApp-iOS)과 서로 유사한 개발 규칙 및 기능을 가집니다.
2. Webview와 Native간의 인터페이스가 비동기적으로 동작합니다.
   1. Web에서 **Promise로 호출 및 반환**됩니다.
   2. Native에서는 **Coroutine으로 동작**합니다.
3. **Kotlin의 lambda**로 인터페이스시 동작을 정의할 수 있습니다.
4. 기본 자료형 외, **Array, Map, Model**을 사용할 수 있습니다.
5. 인터페이스가 실행될 스레드 개수를 설정하여, 복수의 동작을 **병렬처리** 할 수 있습니다.
6. 인터페이스 동작이 가능한 Url을 지정하여 **원하지 않는 사이트에서의 Native호출**을 막을 수 있습니다.

외 다양한 기능들 존재

# 인터페이스 기본 사용법

기본적으로 아래와 같은 패턴으로 인터페이스 등록 및 사용이 가능하며,  
모든 인터페이스는 비동기적으로 동작합니다.  
Web에서는 응답이 발생할 때 까지 **pending상태**가 됩니다.

## Web to Nativew

### 인터페이스 등록

Web to Nativew 인터페이스는 웹뷰에 페이지가 로드되기전에 설정되어야합니다.

```kt
// in kotlin
flexWebView.setInterface("funcName") { args ->
    "received from web - ${args[0]?.toString() ?: "no value"}, return to web - ${100}"
}
flexWebView.loadUrl(someUrl)
```

### 인터페이스 사용

```js
// in js
const test = async (req) => {
  const res = await $flex.funcName(req);
  console.log(res); // received from web - 200, return to web - 100
};
test(200);
```

## Natvie to Web

### 인터페이스 등록

1. [FlexHybridApp-Script](https://github.com/Kyun-J/FlexHybridApp-Scripts) 적용시

```js
// in js
$flex.web.funcName = async (req) => {
  return await new Promise((resolve) => {
    setTimeout(() => resolve(`received from web - ${req}`), 100);
  });
};
```

2. 미적용시

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

### 인터페이스 사용

```kt
// in kotlin
flexWebView.evalFlexFunc("funcName", "sendData") { response ->
    Log.i("WebToNative", response.toString()) // received from web - sendData
}
```

# 인터페이스 고급 사용법

## FlexData와 FlexArguemtns

Web에서 전달받은 데이터는 TypeSafe하게 불러오기 위하여 FlexData 객체로 변환됩니다.  
Web to native 인터페이스시, Web의 함수에서 전달하는 Arguments들은 FlexArguemnts 객체로 변환됩니다.  
FlexArguments는 유사 Array형태로, FlexData의 Array입니다.

```js
// in js
$flex.funcName("test1", 2, 3.1, true, [0, 1, 2], { test: "object" });
```

```kt
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

## Model Class 활용

인터페이스에 사용할 데이터를 Model Class로 사용 할 수 있습니다.  
이때 아래의 규칙이 적용됩니다.

1. Model Class는 **반드시 FlexType를 Inheritance** 해야 합니다.
2. Web에서는 Object 형태로 변환됩니다.
3. Class의 멤버변수로 Array는 사용할 수 없으며 리스트 구현 시 Iterable을 상속하는 클래스(ArrayList 등)만 사용할 수 있습니다.
4. Native에서 Model Class를 Arguments로 받을 때는, Web에서 해당 Model에 대응되는 객체 하나만 전달해야 합니다.

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
    Log.i("ModelTest", req.testModel.data2.testInt) // 2000
}
```

```js
// in js
const test = async () => {
  const model = await $flex.modelTest(); // model is { name: 'test', data2: { testInt: 2000 } }
  await $flex.modelArgsTest({ testModel: model });
};
test();
```

## Action 인터페이스

Web to Native 인터페이스시, 지정된 lambda 코드블럭 이외의 코드에서  
값을 리턴할 수 있는 방식입니다.  
Action객체를 통해 원하는 위치의 코드에서 값을 리턴 할 수 있습니다.  
이때, Web에서는 응답이 발생할 때 까지 **pending** 상태가 됩니다.  
또한, 한번 응답한 Action객체는 다시 사용할 수 없습니다.

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

## lambda를 별도 선언하여 사용

인터페이스시 동작을 정의하는 lambda를 쉽게 선언 및 정리하기 위한 기능입니다.  
FlexLambda 클래스의 companion object에 리턴가능한 타입별 lambda가 정의되어있는 함수를 통해 쉽게 사용할 수 있습니다.  
이때, args의 타입을 FlexArguemts 혹은 Model Class로 지정해야 합니다.

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

## Web to native 인터페이스 timeout 설정

인터페이스가 비동기로 동작하기 때문에, Web에서는 무한 pending상태로 빠질 우려가 있습니다.  
따라서 인터페이스의 timeout을 설정할 수 있으며, timeout이 발생하면 reject로 전환됩니다.  
timeout을 설정하지 않을 시, 기본값은 60000ms 입니다.

### timeout 기본값 지정

timeout의 기본값을 지정할 수 있습니다. 인터페이스별 timeout이 설정되지 않으면, 해당 값이 기본적으로 설정됩니다.  
0을 설정하면 timeout이 발생하지 않고 무한 대기합니다.  
단위는 ms입니다.

```kt
val timeout = 1000
flexWebView.setInterfaceTimeout(timeout)
```

### 인터페이스별 timeout 지정

인터페이스를 설정 할 때 해당 인터페이스의 timeout을 지정 할 수 있습니다.  
0을 설정하면 timeout이 발생하지 않고 무한 대기합니다.  
단위는 ms입니다.

```kt
val timeout = 200
fletWebView.setInterface("funcName", timeout) {}
```

## 인터페이스 스레드 설정

인터페이스가 동작할 스레드 개수 혹은 CoroutineContext를 설정 할 수 있습니다.  
모든 Native내의 인터페이스 동작은 해당 스레드풀/CoroutineContext로 설정된 CoroutineScope 내에서 동작합니다.  
두 설정을 동시에 지정할 경우, CoroutineContext 설정을 우선합니다.  
설정되지 않을 경우, 모바일 프로세서 수와 동일한 스레드 개수로 설정된 CoroutineScope로 동작합니다.

```kt
flexWebView.setCoroutineContext(mCoroutineContext)
flexWebView.setInterfaceThreadCount(threadCnt)
```

## 인터페이스 이벤트 리스너

인터페이스 모듈 초기화, 인터페이스 성공, 실패, 타임아웃에 대한 이벤트를 청취 할 수 있습니다.

```kt
// Listen for specific events
flexWebView.addFlexEventListener(FlexEvent.EXCEPTION) { view, type, url, funcName, msg ->
    Log.i("Flex EXCEPTION", "type: ${type.name} url: $url funcName: $funcName msg: $msg")
}

// Listen to all events
val AllListener = { view, type, url, funcName, msg ->
    Log.i("Flex ALL Listen", "type: ${type.name} url: $url funcName: $funcName msg: $msg")
}
flexWebView.addFlexEventListener(AllListener)

// Remove specific EventListener
flexWebView.removeFlexEventListener(AllListener)

// Remove all EventListeners
flexWebView.removeAllFlexEventListener()
```

# FlexWebView 기능들

## Default WebSetting

FlexWebView는 WebView의 WebSetting 항목 중 아래 사항이 기본으로 선언되어 있습니다.

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
settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
    setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, true)
}
```

## FlexWebViewClient와 FlexWebChromeClient

FlexWebView는 WebViewClient, WebChromeClient 대신 FlexWebViewClient와 FlexWebChromeClient를 반드시 사용해야 합니다.  
FlexWebChromeClient는 풀 스크린 기능을 제공하며, FlexWebViewClient는 URL제한, AssetLoader설정 등의 기능을 제공합니다.

## URL 제한

의도하지 않은 사이트로의 로드를 막고, URL별 인터페이스 허용 여부를 설정하는 기능입니다.

### BaseUrl

BaseUrl은 url제한을 하지 않고 인터페이스 가능 여부만 설정할 때 사용할 수 있는 기능입니다.  
AllowUrlList와 BaseUrl을 모두 설정하지 않을경우 FlexWebView는 모든 사이트로의 접근을 허용하지만, 인터페이스 기능을 사용할 수 없습니다.  
BaseUrl만 설정할 경우, 모든 사이트로의 접근을 허용하며 BaseUrl에 매치되는 URL에만 인터페이스 기능이 열립니다.

```kt
flexWebVeiw.baseUrl = "www.myurl.com"
```

와일드카드 서브도메인을 포함하는 url을 지정하고자 한다면, 서브도메인을 넣지 않고 .으로 시작하는 url을 세팅하면 됩니다.

```kt
flexWebVeiw.baseUrl = ".myurl.com"
```

### AllowUrlList

AllowUrlList을 설정하면, 설정된 url들과 BaseUrl을 제외한 모든 url의 접근이 차단됩니다.

```kt
flexWebView.addAllowUrl(".myurl.com")
```

URL설정 시 인터페이스를 허용하려면 addAllowUrl함수의 두번째 프로퍼티에 true를 추가하면 됩니다.

```kt
flexWebView.addAllowUrl(".myurl.com", true)
```

FlexWebViewClient의 notAllowedUrlLoad함수를 통해 접근이 제한된 url의 로드를 감지 할 수 있습니다.

```kt
flexWebView.webViewClient = object : FlexWebViewClient() {
    override fun notAllowedUrlLoad(
        view: WebView,
        request: WebResourceRequest?,
        url: String?
    ) {
        super.notAllowedUrlLoad(view, request, url)
    }
}
```

## 로컬 파일 로드

WebView의 로컬 파일 로드 기능을 종합하여 제공합니다.

### AssetLoader

Asset 내의 파일에 접근하기 위한 AssetLoader 간편 설정 기능.

```kt
flexWebview.setAssetsLoaderUse(true, "/assets/")
flexWebView.loadUrl("https://appassets.androidplatform.net/assets/my.html")
```

### FileAccess

기본 WebSetting의 allowFileAccess, allowFileAccessFromFileURLs, allowUniversalAccessFromFileURLs 항목을 한번에 설정하는 기능.

```kt
flexWebView.setAllowFileAccessAndUrlAccessInFile(true)
```

## 풀스크린

js에서 컨트롤하는 풀스크린 기능을 바로 사용 가능합니다.

```js
// in js
document.documentElement.requestFullscreen();
document.exitFullscreen();
```

# js에서의 사용

## $flex Object

\$flex Object는 FlexWebView를 와 Promise 형태로 상호간 인터페이스가 구성되어있는 객체입니다.  
\$flex는 웹뷰에 웹페이지 로드 후, 런타임으로 웹페이지에 선언됩니다.  
\$flex가 로드 완료되는 시점은, window.onFlexLoad함수를 통해 확인할 수 있습니다.
\$flex는 액세스 가능한 모든 하위 프레임에서도 사용 할 수 있습니다. (Ex)Cross-Origin을 위반하지 않는 iframe)  
\$flex Object의 구성 요소는 다음과 같습니다.

```js
window.onFlexLoad; // $flex is called upon completion of loading.
$flex; // Object that contains functions that can call Native area as WebToNative
$flex.version; // get Library version
$flex.web; // Object used to add and use functions to be used for NativeToWeb
$flex.device; // Current Device Info
$flex.isAndroid; // true
$flex.isiOS; // false
$fles.isScript; // false
```

# ToDo

Flutter 버전 FlexHybirdApp
