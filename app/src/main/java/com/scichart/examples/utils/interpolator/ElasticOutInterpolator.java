//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ElasticOutInterpolator.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils.interpolator;

import android.view.animation.Interpolator;

public class ElasticOutInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return getElasticOut(input, 1, 0.3);
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param amplitude       Amplitude of easing
     * @param period          Animation of period
     * @return easedValue
     */
    private static float getElasticOut(float elapsedTimeRate, double amplitude, double period) {
        if (elapsedTimeRate == 0 || elapsedTimeRate == 1) return elapsedTimeRate;

        double pi2 = Math.PI * 2;
        double s = period / pi2 * Math.asin(1 / amplitude);
        return (float) (amplitude * Math.pow(2, -10 * elapsedTimeRate) * Math.sin((elapsedTimeRate - s) * pi2 / period) + 1);
    }
}
