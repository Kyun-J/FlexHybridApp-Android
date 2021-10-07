[한국어 README](https://github.com/Kyun-J/FlexHybridApp-Android/blob/master/README-ko.md)

[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# FlexibleHybrid

Including simplifying the interface between Webview and Native and asynchronous processing,
It is a library that provides multiple convenience features for Webview use.

# How to add libraries

**minSdkVersion 23**  
**Minimum ChromeVersion 55**

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
