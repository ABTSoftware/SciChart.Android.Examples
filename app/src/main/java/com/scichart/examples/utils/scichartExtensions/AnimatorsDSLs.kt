//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AnimatorsDSLs.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.utils.scichartExtensions

import android.animation.TimeInterpolator
import android.view.animation.LinearInterpolator
import com.scichart.charting.visuals.renderableSeries.*
import com.scichart.extensions.builders.AnimatorBuilderBase
import com.scichart.extensions.builders.AnimatorBuilderBase.*
import com.scichart.extensions.builders.SciChartBuilder

open class SweepAnimator(var interpolator: TimeInterpolator = LinearInterpolator(), var duration: Long = 3000, var startDelay: Long = 350, var animateXOnly: Boolean = false)
data class ScaleAnimator(var zeroLine: Double = 0.0): SweepAnimator()
data class WaveAnimator(var zeroLine: Double = 0.0, var durationOfStepData: Float = 0.5f): SweepAnimator()
data class TranslateAnimator(var offset: Float = 0.0f): SweepAnimator()

fun <T : IRenderableSeries> T.opacityAnimation(init: SweepAnimator.() -> Unit) {
    val animator = SweepAnimator().apply(init)
    animator(SciChartBuilder.instance().newOpacityAnimator(this), animator)
        .start()
}

// region Sweep Animators
fun <T : XyRenderableSeriesBase> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyyRenderableSeriesBase> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyzRenderableSeriesBase> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : HlRenderableSeriesBase> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : FastFixedErrorBarsRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : OhlcRenderableSeriesBase> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedColumnRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedMountainRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineLineRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineMountainRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineBandRenderableSeries> T.sweepAnimation(init: SweepAnimator.() -> Unit) = sweepAnimation(SciChartBuilder.instance().newAnimator(this), init)

fun <TBuilder : RenderPassDataAnimatorBuilder<TBuilder>> sweepAnimation(builder: TBuilder, init: SweepAnimator.() -> Unit) {
    val animator = SweepAnimator().apply(init)
    animator(builder, animator)
        .withSweepTransformation(animator.animateXOnly)
        .start()
}
// endregion

// Scale Animators
fun <T : XyRenderableSeriesBase> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyyRenderableSeriesBase> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyzRenderableSeriesBase> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : HlRenderableSeriesBase> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : FastFixedErrorBarsRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : OhlcRenderableSeriesBase> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedColumnRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedMountainRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineLineRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineMountainRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineBandRenderableSeries> T.scaleAnimation(init: ScaleAnimator.() -> Unit) = scaleAnimation(SciChartBuilder.instance().newAnimator(this), init)

fun <TBuilder : RenderPassDataAnimatorBuilder<TBuilder>> scaleAnimation(builder: TBuilder, init: ScaleAnimator.() -> Unit) {
    val animator = ScaleAnimator().apply(init)
    animator(builder, animator)
        .withScaleTransformation(animator.zeroLine)
        .start()
}

// Wave Animators
fun <T : XyRenderableSeriesBase> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyyRenderableSeriesBase> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyzRenderableSeriesBase> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : HlRenderableSeriesBase> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : FastFixedErrorBarsRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : OhlcRenderableSeriesBase> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedColumnRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedMountainRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineLineRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineMountainRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineBandRenderableSeries> T.waveAnimation(init: WaveAnimator.() -> Unit) = waveAnimation(SciChartBuilder.instance().newAnimator(this), init)

fun <TBuilder : RenderPassDataAnimatorBuilder<TBuilder>> waveAnimation(builder: TBuilder, init: WaveAnimator.() -> Unit) {
    val animator = WaveAnimator().apply(init)
    animator(builder, animator)
        .withWaveTransformation(animator.zeroLine, animator.durationOfStepData)
        .start()
}

// TranslateX Animators
fun <T : XyRenderableSeriesBase> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyyRenderableSeriesBase> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyzRenderableSeriesBase> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : HlRenderableSeriesBase> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : FastFixedErrorBarsRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : OhlcRenderableSeriesBase> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedColumnRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedMountainRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineLineRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineMountainRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineBandRenderableSeries> T.translateXAnimation(init: TranslateAnimator.() -> Unit) = translateXAnimation(SciChartBuilder.instance().newAnimator(this), init)

fun <TBuilder : RenderPassDataAnimatorBuilder<TBuilder>> translateXAnimation(builder: TBuilder, init: TranslateAnimator.() -> Unit) {
    val animator = TranslateAnimator().apply(init)
    animator(builder, animator)
        .withTranslateXTransformation(animator.offset)
        .start()
}

// TranslateY Animators
fun <T : XyRenderableSeriesBase> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyyRenderableSeriesBase> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : XyzRenderableSeriesBase> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : HlRenderableSeriesBase> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : FastFixedErrorBarsRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : OhlcRenderableSeriesBase> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedColumnRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : StackedMountainRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineLineRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineMountainRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)
fun <T : SplineBandRenderableSeries> T.translateYAnimation(init: TranslateAnimator.() -> Unit) = translateYAnimation(SciChartBuilder.instance().newAnimator(this), init)

fun <TBuilder : RenderPassDataAnimatorBuilder<TBuilder>> translateYAnimation(builder: TBuilder, init: TranslateAnimator.() -> Unit) {
    val animator = TranslateAnimator().apply(init)
    animator(builder, animator)
        .withTranslateYTransformation(animator.offset)
        .start()
}

private fun <TBuilder : AnimatorBuilderBase<TBuilder>> animator(builder: TBuilder, animator: SweepAnimator): TBuilder {
    return builder
        .withInterpolator(animator.interpolator)
        .withDuration(animator.duration)
        .withStartDelay(animator.startDelay)
}