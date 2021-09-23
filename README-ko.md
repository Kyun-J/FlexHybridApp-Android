# Readme 재 작성 중

# 0.8 버전과 호환되지 않는 부분이 있음

# ToDo

1. Interface Event Listener 적용 (완료)
2. Interface각각 개별 설정 적용 (완료)
3. Model을 사용하는 인터페이스 (취소)
4. <u>_Flutter 버전 FlexHybirdApp_</u> (추진중)

# FlexibleHybrid

FlexibleHybridApp은 Web, Native 상호간의 Interface을 Promise로 구현하는 등, HybridApp을 개발하기 위해 여러 편의 기능을 제공하는 라이브러리입니다.

# 라이브러리 추가 방법

**minSdkVersion 23**  
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
5. Web에서 Native 호출시, **Native 코드 블럭은 Custom Coroutine** 안에서 동작하며 JavascriptInterface의 JavaBridge Thread와 다르게 Multi Thread 로 동작하며 병렬로 처리됩니다.
6. FlexWebView에 BaseUrl을 지정하여, **타 사이트 및 페이지에서 Native와 Interface하는 것을 방지**할 수 있습니다.
7. FlexWebView에 페이지가 최초로 로드되어 화면에 나타난 후에는 WebToNative 인터페이스를 추가 할 수 없습니다.

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

상세한 사용법은 [Flex 인터페이스 구현](#Flex-인터페이스-구현) 항목을 참고하세요.
