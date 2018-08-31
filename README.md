# FlipView

适用 Android 开发，用于轮播图等场景。

## 1. 特性

* View 的创建与数据绑定等由 FlipAdapter 决定（2.0.0 之前为 PagerAdapter）。
* 支持 title 和 indicator 等的自定义。
* 支持自动轮播，即在窗口可见时自动播放，不可见时自动暂停。
* 支持用户触摸 FlipView 时暂停轮播，离开时恢复。
* 支持无限循环轮播和滑动。
* 支持反向轮播。

## 2. xml 属性与方法说明

| xml 属性             | 格式类型  | 方法                                | 功能                                                         |
| -------------------- | --------- | ----------------------------------- | ------------------------------------------------------------ |
| app:autoStart        | boolean   | setAutoStart()/isAutoStart()        | 是否启用自动播放                                             |
| app:pauseOnTouch     | boolean   | setPauseOnTouch()/isPauseOnTouch()  | 是否启用在用户触摸 FlipView 时暂停，离开时恢复。             |
| app:flipInterval     | int       | setFlipInterval()/getFlipInterval() | 设置轮播间隔                                                 |
| app:flipperLayout    | reference | 无                                  | LayouRes, 自定义布局中必须含有 ViewPager，且 id 为 "flipper". |
| app:titleEnabled     | boolean   | 无                                  | 是否显示 title，且 title 必须通过 PagerAdater.getPageTitle 返回。 |
| app:titleLayout      | reference | 无                                  | LayouRes, 自定义布局中必须含有 TextView，且 id 为 "title".   |
| app:indicatorEnabled | boolean   | 无                                  | 是否显示 indicator                                           |
| app:indicatorLayout  | reference | 无                                  | LayouRes, 自定义布局中必须含有 TabLayout，且 id 为 "indicator". |
| app:infiniteLoop     | boolean   | 无                                  | 是否启用无限循环轮播，默认为 false.                          |
| app:reverse          | boolean   | setReverse()/isReverse()            | 是否启用反向轮播，默认为 false.                              |

## 3. 使用方法

(1) 在项目的 build.gradle 中配置仓库地址：

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

(2) 添加项目依赖：

```groovy
dependencies {
    implementation 'com.github.ccolorcat:FlipView:v2.0.1'
}
```