
[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

# FlexibleHybrid
FlexibleHybridApp is a library that provides various convenience functions to develop HybridApp, such as implementing Web-> Native Call as a promise.

# Add Library
1. Use JCenter  

Add to the module's build.gradle 
```gradle
dependencies {
    implementation 'app.dvkyun.flexhybridand:flexhybridand:0.1.2'
}
```
1. Use jitpack 

Add to your project build.gradle
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add to the module's build.gradle 
```gradle
dependencies {
        implementation 'com.github.Kyun-J:FlexHybridApp-Android:0.1.2'
}
```

# JavaScriptInterface with Promise Return

To return from JavastriptInterface to Promise type, @JavascriptInterface function must return one of FlexJSCall, FlexJSAction, and FlexJSAsync (Kotlin Only).
```java
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
When writing the code as above, the testCall function receives the result as a promise on the WebView after a delay of 1 second.
```js
const test1 = async () => {
    const t = 0;
    console.log('Send to Native --- ' + t);
    const z = await $flex.testCall(t);
    console.log('Return by Native --- ' + z);
}
```
# `$flex` Object
`$flex` Object is responsible for the interface between Web <-> Native in the Web of FlexHybrid library.  
In `$flex`, functions registered as `addJsInterface(cls: Any)` in FlexWebView are created, and these functions return Promise.
```kt
//in native
@JavascriptInterface
fun likeThis(): FlexJSAction {
.....
}
```
```js
// in js
....
const NatieveValue = await $flex.likeThis();
```
If you create a function in `$flex.web`, you can easily call these functions in Native through `evalFlexFunc` of FlexWebView.
```kt
// in native
flexWebView.evalFlexFunc('WebFunction', 'test')
```
When registering a function in `$flex.web`, it must be registered after window.onload is called.  
```js
// in js
window.onload = function() {
    $flex.web.WebFunction = (msg) => { console.log(msg); }
}
```
The `$flex` Object is automatically generated from the html page loaded by FlexWebView.  
However, `$flex` is created only in part of the page registered as BaseUrl in FlexWebView and is not created when loading other pages.
## $flex 구성요소
#### `$flex.version`
> Get the version of the library.

#### `$flex.addEvent(event, callback)`
> *developing*  
> Add an event listener.

#### `$flex.init()`
> Return to the initial state. All added events and `$flex.web` functionality will be deleted.

#### `$flex.web`
> If you add a function through the `$flex.web` object argument, you can easily call those functions from Native through `evalFlexFunc`.


# Native Class
## **FlexWebView**
#### `initialize()`
> Returns the WebView Settings set in FlexWebView to the initial state.

#### `setBaseUrl(url: String)`
> *If you do not set BaseUrl, FlexWebView will not work.*  
Set the default url like https://github.com.

#### `getBaseUrl(): String?`
> Return the set BaseUrl.

#### `addJsInterface(cls: Any)`
> Adds the interface containing the @JavascriptInterface function as an argument and adds an interface.  
Unlike the existing WebView's addJavascriptInterface () function, it does not receive the name parameter.  
The interface added through this function can be called through the `$flex` Object on the web.
> ```js
> const NatieveValue = await $flex.likeThis();
> ```

#### `getWebChromeClient(): FlexWebChromeClient`
> Unlike base WebView, it returns FlexWebChromeClient.

#### `setWebChromeClient(client: WebChromeClient)`
> WebChromeClient is taken as an argument, but an Execption occurs if it cannot be cast to FlexWebChromeClient.

#### `getWebViewClient(): FlexWebViewClient`
> Unlike base WebView, it returns FlexWebViewClient.

#### `setWebViewClient(client: WebViewClient)`
> WebViewClient is taken as an argument, but an Execption occurs if it cannot be cast to FlexWebViewClient.

#### `evalFlexFunc(funcName: String, prompt: Any?)` 
#### `evalFlexFunc(funcName: String)`
> The function registered under the `$flex.web` Object on the Web is called.  
Prompt receives Any as an argument, but it is converted to String and delivered to the actual JS function.

#### `flexInitInPage()`
> Initialize the `$flex` Object. Same as `$flex.init()`.

#### `setToGlobalFlexWebView(set: Boolean)`
> *This function needs attention*  
Register FlexWebView statically as global.  
You can call the FlexWebView registered through `FlexStatic.getGlobalFlexWebView()`.

## **FlexWebViewClient, FlexWebChromeClient**
> It can be used in the same way as Android's WebViewClient and WebChromeClient.  
However, FlexWebView must apply the above two classes.

## **FlexJSAction**
> FlexJSAction requires FlexWebView.  
FlexJSAction has more flexible usage rules than FlexJSCall and FlexJSAsync.  
There are no parameters related to threads such as Callable and Defferd, and when `Ready` is completed, the Retrun value is delivered directly to the Web in the form of promise.  
It is suitable for developers to use Flow directly.

#### `FlexJSAction()`
> *This constructor needs attention*  
Create by registering GlobalFlexWebView in FlexJSAction.  
If there is no Globally registered FlexWebView, an Execption occurs.

#### `FlexJSAction(webView: FlexWebView)`
> Create FlexJSAction.

#### `setWebView(webView: FlexWebView): FlexJSAction`
> Reset the registered FlexWebView.

#### `setReadyListener(listener: () -> Any)`
> Register ReadyListener. When FlexJSAction is `Ready`, the interface is called.  
The value returned from the interface is delivered to the web in the form of a promise.  

#### `isReady(): Boolean`
> Returns whether FlexJSAction is `Ready`.

#### `send(value: Any?)`
> Pass values directly to the web. Execption occurs if FlexJSAction is not ready.  

## **FlexJSCall**
> FlexJSCall requires FlexWebView object.  
Native work through ThreadPoolExecutor and Callable.  
When the native operation ends, the Retrun value is delivered to the registered FlexWebView in the form of promise.

#### `FlexJSCall()`
> *This constructor needs attention*  
Create by registering GlobalFlexWebView in FlexJsCall.  
If there is no Globally registered FlexWebView, an Execption occurs.

#### `FlexJSCall(webView: FlexWebView)`
> Create FlexJSCall.

#### `FlexJSCall(webView: FlexWebView, executor: ThreadPoolExecutor)`
> Create FlexJSCall and set Callable to operate within a specific ThreadPoolExecutor.

#### `setExecutor(executor: ThreadPoolExecutor): FlexJSCall`
> Set Callable to work within a specific ThreadPoolExecutor.  
If not set, it operates in its own ThreadPoolExecutor.

#### `setWebView(webView: FlexWebView): FlexJSCall`
> Reset the registered FlexWebView.

#### `call(callable: Callable<*>): FlexJSCall`
> Register callable. The return value in the callable is delivered to the web in the form of promise.

## **FlexJSAsync**
> *This class is for Kotlin only.*  
FlexJSAsync requires FlexWebView object.  
Native work is performed through Deferred of CoroutineScope.  
When the native operation ends, the Retrun value is delivered to the registered FlexWebView in the form of promise.

#### `FlexJSAsync()`
> *This constructor needs attention*  
Create by registering GlobalFlexWebView in FlexJSAsync.  
If there is no Globally registered FlexWebView, an error occurs.

#### `FlexJSAsync(webView: FlexWebView)`
> Create FlexJSAsync.

#### `FlexJSAsync(webView: FlexWebView, scope: CoroutineScope)`
> Create FlexJSAsync, and set the scope in which Defferd object will await.

#### `setScope(scope: CoroutineScope): FlexJSAsync`
> Sets the scope in which the Deferred object will be awaited.  
If not set, it operates in its own CoroutineScope (Dispatchers.IO).

#### `setWebView(webView: FlexWebView): FlexJSAsync`
> Reset the registered FlexWebView.

#### `launch(deferred: Deferred<*>): FlexJSAsync`
> Register the Deferred object. Deferred return value is delivered to the web in the form of promise.