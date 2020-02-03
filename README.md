# SciChart.Android.Examples

Examples, Showcase Applications and Tutorials for [SciChart.Android](https://www.scichart.com): High Performance Realtime [Android Chart Library](https://www.scichart.com/android-chart-features). 

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

## Scichart Showcase

The SciChart Showcase demonstrates some featured apps which show the speed, power and flexibility of the SciChart's [Android Chart library](https://www.scichart.com/android-chart-features). This showcase is written in Kotlin and is designed to be a demonstration of what SciChart can do. Examples include:

<img src="https://www.scichart.com/wp-content/uploads/2017/04/pixel-android-showcase-audio-analyzer-2.jpg" Width="420" Alt="Android Audio Analyzer powered by SciChart"/>

<img src="https://www.scichart.com/wp-content/uploads/2017/04/pixel-android-showcase-ecg-monitor-2.jpg" Width="420" Alt="Android 4-Channel Realtime ECG powered by SciChart"/>

* SciChart ECG: Realtime, 4-channel ECG for medical apps 
* SciChart Audio Analyzer: Realtime Audio Analyzer which records the mic, and presents Frequency Spectrum + Spectrogram on a live updating heatmap
* plus more coming soon!

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

## SciChart Android Tutorials 

We now have a set of Tutorials targetting SciChart Android v3.x over on our website. Please find a list of 2D and 3D tutorials below:

![SciChart Android Tutorials](https://www.scichart.com/wp-content/uploads/2020/02/Android-Documentation-Version-3.png)

* [Tutorial 01 - Adding SciChart libraries as dependencies](https://www.scichart.com/documentation/android/current/Tutorial%2001%20-%20Adding%20SciChart%20libraries%20as%20dependencies.html)
* [Tutorial 02 - Creating a Chart](https://www.scichart.com/documentation/android/current/Tutorial%2002%20-%20Creating%20a%20Chart.html)
* [Tutorial 03 - Adding Series to a Chart](https://www.scichart.com/documentation/android/current/Tutorial%2003%20-%20Adding%20Series%20to%20a%20Chart.html)
* [Tutorial 04 - Adding Zooming, Panning Behavior](https://www.scichart.com/documentation/android/current/Tutorial%2004%20-%20Adding%20Zooming,%20Panning%20Behavior.html)
* [Tutorial 05 - Adding Tooltips and Legends](https://www.scichart.com/documentation/android/current/Tutorial%2005%20-%20Adding%20Tooltips%20and%20Legends.html)
* [Tutorial 06 - Adding Realtime Updates](https://www.scichart.com/documentation/android/current/Tutorial%2006%20-%20Adding%20Realtime%20Updates.html)
* [Tutorial 07 - Adding Annotations](https://www.scichart.com/documentation/android/current/Tutorial%2007%20-%20Adding%20Annotations.html)
* [Tutorial 08 - Adding Multiple Axis](https://www.scichart.com/documentation/android/current/Tutorial%2008%20-%20Adding%20Multiple%20Axis.html)
* [Tutorial 09 - Linking Multiple Charts](https://www.scichart.com/documentation/android/current/Tutorial%2009%20-%20Linking%20Multiple%20Charts.html)
* [3D Tutorial 01 - Charting Concepts](https://www.scichart.com/documentation/android/current/3D%20Tutorial%20%2001%20-%20Charting%20Concepts.html)
* [3D Tutorial 02 - Add SciChart Libraries to Android Studio](https://www.scichart.com/documentation/android/current/3D%20Tutorial%20%2002%20-%20Add%20SciChart%20Libraries%20to%20Android%20Studio.html)
* [3D Tutorial 03 - Creating a Chart and Add a Data Series](https://www.scichart.com/documentation/android/current/3D%20Tutorial%20%2003%20-%20Creating%20a%20Chart%20and%20Add%20a%20Data%20Series.html)
* [3D Tutorials 04 - Changing the Axes Length and Chart Pitch and Yaw](https://www.scichart.com/documentation/android/current/3D%20Tutorial%2004%20-%20Changing%20the%20Axes%20Length%20and%20Chart%20Pitch%20and%20Yaw.html)
* [3D Tutorial 05 - Zooming and Rotating](https://www.scichart.com/documentation/android/current/3D%20Tutorial%2005%20-%20Zooming%20and%20Rotating.html)
* [3D Tutorial 06 - Cursors and Tooltips](https://www.scichart.com/documentation/android/current/3D%20Tutorial%2006%20-%20Cursors%20and%20Tooltips.html)
* [3D Tutorial 07 - Plotting Realtime Data](https://www.scichart.com/documentation/android/current/3D%20Tutorial%2007%20-%20Plotting%20Realtime%20Data.html)

## Tech Support and Help 

SciChart Android is a commercial chart control with world-class tech support. If you need help integrating SciChart to your Android apps, [Contact Us](https://www.scichart.com/contact-us) and we will do our best to help! 


