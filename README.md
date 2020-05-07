
[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

FlexibleHybridApp is a library that provides several features to develop HybridApp, including the implementation of Web<->Native Interface as Promise.

# How to add libraries

**minSdkVersion 19**

1. Enable JCenter

Add the following to the build.gradle of the module.
```Gradle
Dependencies {
    Implementation 'app.dvkyun.flexhybrid:flexhybrid:0.2'
}
```
2. Enable jitpack

Add the following to the project build.gradle
```Gradle
allprojects {
    Reporters {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Then add the following to the build.gradle of the module:
```Gradle
Dependencies {
    Implementation 'com.github.Kyun-J:FlexHybridApp-Android:0.2'
}
```

# Interface Key Features
Basically, it compensates for the shortcomings of Android's JavascriptInterface and has additional restrictions.
1. When a native function is called from the Web, **the return of the native function is passed to Promise** on the Web.
2. When calling the Web function from Native, the return value **can be passed Async** from Web to Native.
3. In addition to annotations, **you can add an interface by invoking a function that receives the Lambda (interface of Java) of Kotlin as a factor**.
4. 4. In addition to the basic data type, **JS' array can be delivered as (JSONARray, Array, List) and JS' Object as (JSONObject, Map)**.
5. When calling Native from the Web, **Native code blocks operate within CoroutineScope (Dispatchers.Default) and perform about 1.5 to 2 times better than JavaBridgeThread** in JavascriptInterface.
6. By specifying BaseUrl in FlexWebView, you can **prevent native and interface on other sites and pages**.
7. You cannot add an interface after the page is first loaded into FlexWebView and appears on the screen.

# Interface implementation
## WebToNative Interface
The WebToNative interface of FelxWebView includes the Normal Interface and the Action Interface.
### ***Normal Interface***
Normal Interface by default declares:
```kt
// in Kotlin
flexWebView.setInterface("Normal") // "Normal" features the function name in Web JavaScript.
{   argents ->
    // Arguemnts Data from web. Type is JSONARray
    Return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
The first factor in 'setInterface' is the function name on the web, followed by the lambda, which is the code block in which the function operates.
Arguments delivered to lambda are JSONARray objects that contain the values delivered when calling a function from web.
You can call a function on the web as shown below.
```js
// in web javascript
...
const res = await $flex.Normal("data1",2,false");
// res is "HiFlexWeb"
```
The ("data1",2,false) value passed to the function above is communicated by keeping the data type intact in the aggregates of the lambda.

### ***Action Interface***
Action Interface is almost the same as Normal Interface, but it delivers a value return to the Web when the action object calls the `promiseReturn` method.
```kt
// in Kotlin
var mAction: FlexAction? = null
...
flexWebView.setAction("Action")
{   action, items ->
    // action is FlexAction Object
    mAction = action
}
...
// Returns to the Web when calling promiseReturn.
mAction.promiseReturn(arrayOf("FlexAction!!!",100));
mAction = null
```
If the `promiseReturn` method is not called, web must be careful to call `promiseReturn` when using Action Interface because the function continues to be pended.
In addition, FlexAction objects that have already been called `promiseReturn` should not be called more than twice because they will cause an Exception on `promiseReturn` re-call.
```js
// in web javascript
....
first res = await $flex.Action(); // Pending until promiseReturn is called...
// res is ["FlexAction!!, 100]
```

# Adding...
