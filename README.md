# SciChart.Android.Examples

Examples, Showcase Applications and Tutorials for [SciChart.Android](https://www.scichart.com): High Performance Realtime [Android Chart Library](https://www.scichart.com/android-chart-features). 

![SciChart Android ECG Example](https://abtsoftware-wpengine.netdna-ssl.com/wp-content/uploads/2016/06/ECGMonitorDemo_framed-e1467216503738.png)
![SciChart Android Candlestick Example](https://abtsoftware-wpengine.netdna-ssl.com/wp-content/uploads/2016/08/RealTimetickingStockChart.png)
![SciChart Android Heatmap Example](https://abtsoftware-wpengine.netdna-ssl.com/wp-content/uploads/2016/06/HeatmapChart_framed.png)
![SciChart Android Multi XAxis Example](https://abtsoftware-wpengine.netdna-ssl.com/wp-content/uploads/2016/08/MultiXAxes_framed-2-e1470382586249.png)

Android Chart Examples are provided in Java & Kotlin. If you are looking for other platforms then please see here:

* [iOS Charts](https://github.com/ABTSoftware/SciChart.iOS.Examples) (Swift / Objective C)
* [WPF Charts](https://github.com/ABTSoftware/SciChart.WPF.Examples) (C# / WPF)
* [Xamarin Charts](https://github.com/ABTSoftware/SciChart.Xamarin.Examples) (C#) BETA!
* [NativeScript Charts](https://github.com/ABTSoftware/SciChart.NativeScript.Examples) (TypeScript / Javascript) BETA!

### Note: Maven Feed Setup

To build, you will need an internet connection to download Maven dependencies. Maven depends are listed in the [build.gradle files within the examples application](https://github.com/ABTSoftware/SciChart.Android.Examples/blob/master/v2.x/Examples/app/build.gradle), for example: 

```
repositories {
    mavenLocal()
    mavenCentral()
    maven { url 'https://www.myget.org/F/abtsoftware/maven' }
}
dependencies {
    compile (group: 'com.scichart.library', name: 'core', version: '2.0.0.1806', ext: 'aar')
    compile (group: 'com.scichart.library', name: 'data', version: '2.0.0.1806', ext: 'aar')
    compile (group: 'com.scichart.library', name: 'drawing', version: '2.0.0.1806', ext: 'aar')
    compile (group: 'com.scichart.library', name: 'charting', version: '2.0.0.1806', ext: 'aar')
    compile (group: 'com.scichart.library', name: 'extensions', version: '2.0.0.1806', ext: 'aar')
```

_where latest version number can be found at our [SciChart/ABTSoftware Maven feed page](https://www.myget.org/feed/abtsoftware/package/maven/com.scichart.library/charting)_

# Repository Contents

## Scichart Showcase

The SciChart Showcase demonstrates some featured apps which show the speed, power and flexibility of the SciChart's [Android Chart library](https://www.scichart.com/android-chart-features). This showcase is written in Kotlin and is designed to be a demonstration of what SciChart can do. Examples include:

![Android Audio Analyzer powered by SciChart](https://www.scichart.com/wp-content/uploads/2017/04/pixel-android-showcase-audio-analyzer.png)

![Android 4-channel Realtime ECG powered by SciChart](https://www.scichart.com/wp-content/uploads/2017/04/pixel-android-showcase-ecg-monitor.png)

* SciChart ECG: Realtime, 4-channel ECG for medical apps 
* SciChart Audio Analyzer: Realtime Audio Analyzer which records the mic, and presents Frequency Spectrum + Spectrogram on a live updating heatmap
* + more coming soon!

## SciChart Android Examples Suite

The SciChart Android Examples suite contain developer example code in Java to help you get started as soon as possible with SciChart.Android. 

![SciChart Android Examples Suite](https://www.scichart.com/wp-content/uploads/2017/04/scichart-android-examples-header-cropped-for-github2.png)

Chart types include: 

* [Android Line Chart](https://www.scichart.com/android-line-chart-example/)
* [Android Band Chart](https://www.scichart.com/android-chart-example-band-series-chart/)
* [Android Candlestick Chart](https://www.scichart.com/android-candlestick-chart-example/) 
* [Android Column Chart](https://www.scichart.com/android-column-chart-example/)
* [Android Mountain / Area Chart](https://www.scichart.com/android-mountain-chart-example/)
* [Android Scatter Chart](https://www.scichart.com/android-scatter-chart-example/)
* [Android Impulse / Stem Chart](https://www.scichart.com/android-impulse-stem-chart-example/)
* Android Bubble Chart
* [Android Error Bars Chart](https://www.scichart.com/android-chart-example-error-bars/)
* [Android Fan Chart](https://www.scichart.com/android-chart-example-fan-chart/)
* [Android Heatmap Chart](https://www.scichart.com/android-heatmap-chart-example/)
* Android Stacked Mountain Chart 
* Android Stacked Column Chart
* Android 100% Stacked Mountain Chart 
* Android 100% Stacked Column Chart
* [Android Stock Charts](https://www.scichart.com/android-multi-pane-stock-charts/)
* [Android Chart Legends](https://www.scichart.com/android-chart-legends-api-example/)

## SciChart Android Tutorials 

We now have a set of Tutorials targetting SciChart Android v2.x over on our website. Please find a list of tutorials below:

![SciChart Android Tutorials](https://www.scichart.com/wp-content/uploads/2017/04/scichart-android-tutorials-image.png)

* [Tutorial 01 - Adding SciChart libraries as dependencies](https://www.scichart.com/documentation/android/v2.x/Tutorial%2001%20-%20Adding%20SciChart%20libraries%20as%20dependencies.html)
* [Tutorial 02 - Creating a Chart](https://www.scichart.com/documentation/android/v2.x/Tutorial%2002%20-%20Creating%20a%20Chart.html)
* [Tutorial 03 - Adding Series to a Chart](https://www.scichart.com/documentation/android/v2.x/Tutorial%2003%20-%20Adding%20Series%20to%20a%20Chart.html)
* [Tutorial 04 - Adding Zooming, Panning Behavior](https://www.scichart.com/documentation/android/v2.x/Tutorial%2004%20-%20Adding%20Zooming,%20Panning%20Behavior.html)
* [Tutorial 05 - Adding Tooltips and Legends](https://www.scichart.com/documentation/android/v2.x/Tutorial%2004%20-%20Adding%20Zooming,%20Panning%20Behavior.html)
* [Tutorial 06 - Adding Realtime Updates](https://www.scichart.com/documentation/android/v2.x/Tutorial%2006%20-%20Adding%20Realtime%20Updates.html)
* [Tutorial 07 - Adding Annotations](https://www.scichart.com/documentation/android/v2.x/Tutorial%2006%20-%20Adding%20Realtime%20Updates.html)
* [Tutorial 08 - Adding Multiple Axis](https://www.scichart.com/documentation/android/v2.x/Tutorial%2008%20-%20Adding%20Multiple%20Axis.html)
* [Tutorial 09 - Linking Multiple Charts](https://www.scichart.com/documentation/android/v2.x/Tutorial%2009%20-%20Linking%20Multiple%20Charts.html)

## Tech Support and Help 

SciChart Android is a commercial chart control with world-class tech support. If you need help integrating SciChart to your Android apps, [Contact Us](https://www.scichart.com/contact-us) and we will do our best to help! 


