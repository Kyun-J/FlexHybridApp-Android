[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# Rewriting ReadMe...

# There are parts incompatible with version 0.8

# ToDo

1. Interface Event Listener (complete)
2. Individual settings applied to each interface (complete)
3. Interface using Model (cancel)
4. <u>_Flutter version of FlexHybirdApp_</u> (in progress)

# FlexibleHybrid

FlexibleHybridApp is a library that provides various convenience functions to develop HybridApp, such as implementing interface between WebPage and Native with promises.

# How to add libraries

**minSdkVersion 23**  
**Minimum ChromeVersion 55**

1. jitpack

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
5. When calling Native from Web, **Native code block operates within Custom Coroutine** and operates as Multi Thread differently from JavaBridge Thread of JavascriptInterface and is processed in Concurrent.
6. By specifying BaseUrl in FlexWebView, you can **prevent native and interface on other sites and pages**.
7. You cannot add an interface after the page is first loaded into FlexWebView and appears on the screen.

# $flex Object

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

For detailed usage, refer to [Flex Interface Implementation](#Flex-Interface-Implementation).
