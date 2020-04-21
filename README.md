# FlexibleHybrid

FlexibleHybridApp은 Web->Native Call을 Promise로 구현하는 등, HybridApp을 개발하기 위해 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법
1. JCenter 사용  

모듈의 build.gradle에 다음을 추가.
```
dependencies {
    implementation 'app.dvkyun.flexhybridand:flexhybridand:0.1.1-1'
}
```
2. jitpack 사용  

프로젝트 build.gradle에 다음을 추가
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
그후 모듈의 build.gradle에 다음을 추가
```
dependencies {
        implementation 'com.github.Kyun-J:FlexHybridApp-Android:0.1.1-1'
}
```

# JSInterface Return Promise

JavastriptInterface에서 Promise 타입으로 Return 받기 위해선, 먼저 @JavascriptInterface 함수가 FlexJSCall 혹은 FlexJSAsync(Kotlin Only)를 Return 해야 합니다.
```
@JavascriptInterface
public FlexJSCall testCall(final int input) {
    return new FlexJSCall().call(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            Thread.sleep(1000);
            return input + 1;
        }
    });
}
```
위와 같이 코드를 작성할 경우, testCall 함수는 1초의 delay 후, WebView 상에서 Promise로 결과를 Return 받습니다.
```
const test1 = async () => {
    const t = 0;
    console.log('Send to Native --- ' + t);
    const z = await $flex.testCall(t);
    console.log('Return by Native --- ' + z);
}
```
# Native 클래스 상세
## **FlexWebView**
### `initialize()`
FlexWebView에 세팅된 Settings를 초기 상태로 되돌립니다.

### `setBaseUrl(url: String)`
https://github.com 처럼 기본이 되는 url을 설정합니다  
**BaseUrl을 세팅하지 않으면, FlexWebView는 동작하지 않습니다.**

### `getBaseUrl(): String?`
현재 BaseUrl을 Return합니다

### `addJsInterface(cls: Any)`
@JavascriptInterface 함수가 포함된 class를 인자로 받아 인터페이스를 추가합니다.  
기존 WebView의 addJavascriptInterface()함수와 달리 name 파라미터를 받지 않습니다.  
이 함수를 통해 추가된 인터페이스는 Web의 Js에서 `$flex` Object를 통해 Call할 수 있습니다.

### `getWebChromeClient(): FlexWebChromeClient`
기존 WebView와 달리 FlexWebChromeClient를 Retrun합니다

### `setWebChromeClient(client: WebChromeClient)`
WebChromeClient를 인자로 받지만, 해당 객체가 FlexWebChromeClient로 Cast되지 못하면 오류가 발생합니다.

### `getWebViewClient(): FlexWebViewClient`
기존 WebView와 달리 FlexWebViewClien를 Retrun합니다

### `setWebViewClient(client: WebViewClient)`
WebChromeClient를 인자로 받지만, 해당 객체가 FlexWebViewClient로 Cast되지 못하면 오류가 발생합니다.

### `evalFlexFunc(funcName: String, prompt: Any?)`
### `evalFlexFunc(funcName: String)`
Web상에서 `$flex.web` Object 아래에 등록한 함수를 호출합니다.  
Prompt는 Any를 인자로 받지만, 실제 JS 함수에는 String으로 변환되어 전달됩니다.

### `flexInitInPage()`
FlexWebView의 `$flex` Object 를 초기화합니다.  
`$flex.web`아래의 함수는 전부 사라집니다.

### `setToGlobalFlexWebView(set: Boolean)`
**이 함수는 주의가 필요합니다**  
FlexWebView를 Static하게 global로 등록합니다.  
FlexStatic.getGlobalFlexWebView()을 통해 등록한 FlexWebView를 호출할 수 있습니다.

---

## **FlexWebViewClient, FlexWebChromeClient**
Android의 WebViewClient 및 WebChromeClient와 동일하게 사용 가능합니다.  
다만 FlexWebView는 반드시 위 두 클래스를 적용하여야 합니다.

---

## **FlexJSCall**
FlexJSCall은 FlexWebView 객체가 필수로 필요합니다.  
ThreadPoolExecutor 및 Callable을 통해 Native 작업을 실시합니다.  
Native 작업이 종료되면, 등록된 FlexWebView에 Async 하게 Promise Retrun을 합니다.  
### `FlexJSCall()`
**이 생성자는 주의가 필요합니다**  
FlexJsCall에 GlobalFlexWebView를 등록하여 생성합니다.  
Global하게 등록된 FlexWebView가 없으면, 오류가 발생합니다.
### `FlexJSCall(webView: FlexWebView)`
FlexJSCall을 생성합니다.
### `FlexJSCall(webView: FlexWebView, executor: ThreadPoolExecutor)`
FlexJSCall을 생성하며, Callable을 특정 ThreadPoolExecutor 안에서 동작하도록 설정합니다.
### `setExecutor(executor: ThreadPoolExecutor): FlexJSCall`
Callable을 특정 ThreadPoolExecutor 안에서 동작하도록 설정합니다.
설정하지 않을 시, 자체 ThreadPoolExecutor안에서 동작합니다.
### `setWebView(webView: FlexWebView): FlexJSCall`
등록된 WebView를 재설정합니다.
### `call(callable: Callable<*>): FlexJSCall`
callable을 등록합니다. callable내의 return을 통해 Async하게 Return됩니다.

---

## **FlexJSAsync**
**이 클래스는 Kotlin 전용입니다.**  
FlexJSAsync은 FlexWebView 객체가 필수로 필요합니다.  
CoroutineScope의 Deferred를 통해 Native 작업을 실시합니다.  
Native 작업이 종료되면, 등록된 FlexWebView에 Async 하게 Promise Retrun을 합니다.  
### `FlexJSAsync()`
**이 생성자는 주의가 필요합니다**  
FlexJSAsync에 GlobalFlexWebView를 등록하여 생성합니다.  
Global하게 등록된 FlexWebView가 없으면, 오류가 발생합니다.
### `FlexJSAsync(webView: FlexWebView)`
FlexJSAsync를 생성합니다.
### `FlexJSAsync(webView: FlexWebView, scope: CoroutineScope)`
FlexJSAsync를 생성하며, Defferd 객체가 await 동작할 scope를 설정합니다.
### `setScope(scope: CoroutineScope): FlexJSAsync`
Defferd 객체가 await 동작할 scope를 설정합니다.  
설정하지 않을 시, 자체 CoroutineScope(Dispatchers.IO)안에서 동작합니다.
### `setWebView(webView: FlexWebView): FlexJSAsync`
등록된 WebView를 재설정합니다.
### `launch(deferred: Deferred<*>): FlexJSAsync`
Defferd 객체를 등록합니다. 해당 Defferd의 반환값이 Async하게 Return됩니다.


# `$flex` Object 상세
`$flex` Object는 FlexWebView에서 로드한 html 페이지에서 자동 생성됩니다.  
다만 `$flex`는 FlexWebView에서 BaseUrl로 등록한 페이지의 하위에서만 생성되며 그 외의 페이지를 로드할 경우에는 생성되지 않습니다.  
`$flex`안에는 FlexWebView에서 `addJsInterface(cls: Any)`로 등록한 함수들이 생성되어 있으며, 이 함수들은 Promise를 Return 합니다.  
`$flex.web`안에 함수를 생성하면, FlexWebView의 `evalFlexFunc`를 통해 해당 함수들을 손쉽게 호출할 수 있습니다. `$flex.web`에 함수 등록시, window.onload가 호출된 이후에 등록해야 합니다.  
