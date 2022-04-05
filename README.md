# SciChart.Android.Examples
Exampless for [SciChart.Android](https://www.scichart.com): High Performance Realtime [Android Chart Library](https://www.scichart.com/android-chart-features). 

<a href="https://youtu.be/28wtiSRGmXQsc" target="\_blank" Title="SciChart Android Charts Video"><img src="https://www.scichart.com/wp-content/uploads/2017/07/Thumbnail-play.png" Alt="SciChart Android Charts Video"/></a> 

<img Align="Left" src="https://www.scichart.com/wp-content/uploads/2019/12/scichart-android-v3-multipane-stock-chart.png" Width="420" Alt="Android Multi-Pane Stock Charts Example"/>

<img Align="Left" src="https://www.scichart.com/wp-content/uploads/2019/12/scichart-android-v3-annotation-markers-charts.png" Width="420" Alt="Android Annotations are Easy Example"/>

<img Align="Left" src="https://www.scichart.com/wp-content/uploads/2019/08/scichart-android-3d-charts-example-realtime-3d-surfacemesh-chart-landscape-phone.png" Width="420" Alt="Android Realtime 3D Surface Mesh Example"/>

<img Align="Left" src="https://www.scichart.com/wp-content/uploads/2019/09/scichart-android-3d-charts-example-simple-point-lines-chart-landscape-phone.png" Width="420" Alt="Android PointLine 3D Chart Example"/>

<span class="align-center"></span>

Android Chart Examples are provided in Java & Kotlin. If you are looking for other platforms then please see here:

* [iOS Charts](https://github.com/ABTSoftware/SciChart.iOS.Examples) (Swift / Objective C)
* [WPF Charts](https://github.com/ABTSoftware/SciChart.WPF.Examples) (C# / WPF)
* [Xamarin Charts](https://github.com/ABTSoftware/SciChart.Xamarin.Examples) (C#) 

### Note: Maven Feed Setup

To build, you will need an internet connection to download Maven dependencies. Maven depends are listed in the [build.gradle files within the examples application](https://github.com/ABTSoftware/SciChart.Android.Examples/blob/master/v2.x/Examples/app/build.gradle), for example: 

```
repositories {
    mavenLocal()
    mavenCentral()
    maven { url 'https://www.myget.org/F/abtsoftware/maven' }
}
dependencies {
    implementation (group: 'com.scichart.library', name: 'core', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'data', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'drawing', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'charting3d', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'charting', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'extensions', version: '3.0.0.4253', ext: 'aar')
    implementation (group: 'com.scichart.library', name: 'extensions3d', version: '3.0.0.4253', ext: 'aar')
}
```

_where latest version number can be found at our [SciChart/ABTSoftware Maven feed page](https://www.myget.org/feed/abtsoftware/package/maven/com.scichart.library/charting)_

# Repository Contents
## SciChart Android Examples Suite
The SciChart Android Examples suite contain developer example code in Java to help you get started as soon as possible with SciChart.Android. 

![SciChart Android Examples Suite](https://www.scichart.com/wp-content/uploads/2020/02/android-main-page-examples-min-1.png)

Chart types include: 
* [Android Line Chart](https://www.scichart.com/android-line-chart-example/)
* [Android Band Chart](https://www.scichart.com/android-chart-example-band-series-chart/)
* [Android Candlestick Chart](https://www.scichart.com/android-candlestick-chart-example/) 
* [Android Column Chart](https://www.scichart.com/android-column-chart-example/)
* [Android Mountain / Area Chart](https://www.scichart.com/android-mountain-chart-example/)
* [Android Scatter Chart](https://www.scichart.com/android-scatter-chart-example/)
* [Android Impulse / Stem Chart](https://www.scichart.com/android-impulse-stem-chart-example/)
* [Android Bubble Chart](https://www.scichart.com/example/android-bubble-chart-demo/)
* [Android Error Bars Chart](https://www.scichart.com/android-chart-example-error-bars/)
* [Android Fan Chart](https://www.scichart.com/android-chart-example-fan-chart/)
* [Android Heatmap Chart](https://www.scichart.com/android-heatmap-chart-example/)
* [Android Stacked Mountain Chart](https://www.scichart.com/example/android-chart-stacked-mountain-chart-example/)
* [Android Stacked Column Chart](https://www.scichart.com/example/android-chart-stacked-column-chart-example/)
* [Android 100% Stacked Mountain Chart](https://www.scichart.com/example/android-chart-dashboard-style-charts-example/)
* [Android 100% Stacked Column Chart](https://www.scichart.com/example/android-chart-dashboard-style-charts-example/)
* [Android Stacked Grouped Column Chart](https://www.scichart.com/example/android-chart-stacked-column-chart-grouped-side-by-side-example/)
* [Android Stock Charts](https://www.scichart.com/android-multi-pane-stock-charts/)
* [Android Chart Legends](https://www.scichart.com/android-chart-legends-api-example/)
* [Android Pie Charts](https://www.scichart.com/example/android-chart-pie-chart-example/)
* [Android Donut Chart](https://www.scichart.com/example/android-chart-donut-chart-example/)
* [Android Nested Chart](https://www.scichart.com/example/android-chart-nested-chart-example/)
* [Android Simple Bubble 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-bubble/)
* [Android Simple Cylindroid 3D Charts](https://www.scichart.com/example/android-3d-chart-example-simple-cylindroid/)
* [Android Sparse Ellipsoid 3D Charts](https://www.scichart.com/example/android-3d-chart-example-simple-ellipsoid/)
* [Android Simple Point-Cloud 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-point-cloud/)
* [Android Simple Point-Lines 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-point-lines/)
* [Android Simple Polar 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-polar/)
* [Android Simple Scatter 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-scatter/)
* [Android Simple Waterfall 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-waterfall/)
* [Android Sparse Column 3D Chart](https://www.scichart.com/example/android-3d-chart-example-sparse-column/)
* [Android Sparse Impulse Series 3D Chart](https://www.scichart.com/example/android-3d-chart-example-sparse-impulse-series/)
* [Android Uniform Column 3D Chart](https://www.scichart.com/example/android-3d-chart-example-uniform-column/)
* [Android Uniform Impulse 3D Chart](https://www.scichart.com/example/android-3d-chart-example-uniform-impulse-series/)
* [Android Custom Free Surface 3D Chart](https://www.scichart.com/example/android-3d-chart-example-create-custom-free-surface/)
* [Android Simple Uniform Mesh 3D Chart](https://www.scichart.com/example/android-3d-chart-example-simple-uniform-mesh/)
* [Android Surface Mesh Floor and Ceiling 3D Chart](https://www.scichart.com/example/android-3d-chart-example-surface-mesh-floor-and-ceiling/)
* [Android Surface Mesh Palette Provider 3D Chart](https://www.scichart.com/example/android-3d-chart-example-surface-mesh-palette-provider/)
* [Android Surface Mesh With Contours 3D Chart](https://www.scichart.com/example/android-3d-chart-example-surface-mesh-with-contours/)

## Tech Support and Help 
SciChart Android is a commercial chart control with world-class tech support. If you need help integrating SciChart to your Android apps, [Contact Us](https://www.scichart.com/contact-us) and we will do our best to help! 