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

To build, you will need an internet connection to download Maven dependencies. Maven depends are listed in the [build.gradle files within the examples application](https://github.com/ABTSoftware/SciChart.Android.Examples/blob/master/v2.x/Examples/app/build.gradle)

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

# Repository Contents

## Scichart Showcase

The SciChart Showcase demonstrates some featured apps which show the speed, power and flexibility of the SciChart's [Android Chart library](https://www.scichart.com/android-chart-features). This showcase is written in Kotlin and is designed to be a demonstration of what SciChart can do. Examples include:

[TODO IMAGE]

* SciChart ECG: Realtime, 4-channel ECG for medical apps 
* SciChart Audio Analyzer: Realtime Audio Analyzer which records the mic, and presents Frequency Spectrum + Spectrogram on a live updating heatmap
* + more coming soon!

## SciChart Android Examples Suite

The SciChart Android Examples suite contain developer example code in Java to help you get started as soon as possible with SciChart.Android. 

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

... are coming soon!

