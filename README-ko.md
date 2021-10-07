[iOS Version](https://github.com/Kyun-J/FlexHybridApp-iOS)

# README 작성중...

# FlexibleHybrid

Webview와 Native간 인터페이스 간소화, 비동기 처리를 비롯하여  
Webview사용에 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법

**minSdkVersion 23**  
**Minimum ChromeVersion 55**

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

# WebPage

## $flex Object

\$flex Object는 FlexWebView를 와 Promise 형태로 상호간 인터페이스가 구성되어있는 객체입니다.  
\$flex는 웹뷰에 웹페이지 로드 시, 런타임으로 웹페이지에 선언됩니다.  
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
```

# ToDo

Flutter 버전 FlexHybirdApp
