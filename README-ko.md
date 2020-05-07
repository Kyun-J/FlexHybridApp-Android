# FlexibleHybrid

FlexibleHybridApp은 Web<->Native Interface을 Promise로 구현하는 등, HybridApp을 개발하기 위해 여러 편의 기능을 제공하는 라이브러리입니다.

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

# 인터페이스 주요 특징
기본적으로 Android의 JavascriptInterface의 단점을 보완하며 추가 제약이 있습니다.
1. Web에서 Native 함수 호출시, **Native함수의 Return이 Web에 Promise로** 전달됩니다.
2. Native에서 Web함수 호출시, **Web에서 Native로 Async**하게 반환값을 전달 할 수 있습니다.
3. Annotation외에 **Kotlin의 lambda(Java의 Interface)를 인자로 받는 함수**를 호출하여 인터페이스를 추가할 수 있습니다.
4. 기본 자료형 외에 **JS의 Array를 (JSONArray, Array, List)으로, JS의 Object를 (JSONObject, Map)으로** 전달할 수 있습니다.
5. Web에서 Native 호출시, **Native 코드 블럭은 CoroutineScope(Dispatchers.Default)** 안에서 동작하며 JavascriptInterface의 **JavaBridgeThread보다 약 1.5 ~ 2배**정도 나은 성능을 가집니다.
6. FlexWebView에 BaseUrl을 지정하여, **타 사이트 및 페이지에서 Native와 Interface하는 것을 방지**할 수 있습니다.
7. FlexWebView에 페이지가 최초로 로드되어 화면에 나타난 후에는 인터페이스를 추가 할 수 없습니다.

# 인터페이스 구현
## WebToNative 인터페이스
FelxWebView의 WebToNative 인터페이스는 Normal Interface와 Action Interface 가 있습니다. 
### ***Nomal Interface***
Normal Interface는 기본적으로 다음과 같이 선언합니다
```kt
// in Kotlin
flexWebView.setInterface("Normal") // "Normal" becomes the function name in Web JavaScript. 
{ arguments ->
// arguments is Arguemnts Data from web. Type is JSONArray
    return "HiFlexWeb" // "HiFlexWeb" is passed to web in Promise pattern.
}
```
`setInterface`의 첫 인자로 웹에서의 함수 이름을 지정하고 이어지는 lambda는 함수가 동작하는 코드 블럭이 됩니다.  
lambda로 전달되는 arguments는 JSONArray 객체로서 web에서 함수 호출시 전달된 값들이 담겨 있습니다.  
웹에서는 아래와 같이 함수 호출이 가능합니다.
```js
// in web javascript
...
const res = await $flex.Normal("data1",2,false);
// res is "HiFlexWeb"
```
위 함수로 전달한 ("data1",2,false) 값은 lambda의 arguments에 데이터 타입이 그대로 유지되어 전달됩니다.

### ***Action Interface***
Action Interface는 Normal Interface와 거의 비슷하나, Web으로의 값 리턴을 action객체의 `promiseReturn` 메소드를 호출하는 시점에 전달합니다.
```kt
// in Kotlin
var mAction: FlexAction? = null
...
flexWebView.setAction("Action")
{ action, arguments ->
// action is FlexAction Object
    mAction = action
}
...
// Returns to the Web when calling promiseReturn.
mAction.promiseReturn(arrayOf("FlexAction!!!",100));
mAction = null
```
`promiseReturn`메소드가 호출되지 못하면, web에서 해당 함수는 계속 pending된 상태가 되기 때문에 Action Interface를 사용시 `promiseReturn`를 반드시 호출할 수 있도록 주의가 필요합니다.  
또한 이미 `promiseReturn`가 호출되었던 FlexAction 객체는 `promiseReturn` 재 호출시 Exception이 발생하므로 2번 이상 호출하지 않도록 해야합니다.
```js
// in web javascript
....
const res = await $flex.Action(); // Pending until promiseReturn is called...
// res is ["FlexAction!!!", 100]
```

# 추가중...