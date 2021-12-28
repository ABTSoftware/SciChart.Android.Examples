//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CubicInOutInterpolator.java is part of SCICHART®, High Performance Scientific Charts
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

public class CubicInOutInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return getPowInOut(input, 3);
    }

    /**
     * @param elapsedTimeRate Elapsed time / Total time
     * @param pow             pow The exponent to use (ex. 3 would return a cubic ease).
     * @return easedValue
     */
    private static float getPowInOut(float elapsedTimeRate, double pow) {
        if ((elapsedTimeRate *= 2) < 1) {
            return (float) (0.5 * Math.pow(elapsedTimeRate, pow));
        }

        return (float) (1 - 0.5 * Math.abs(Math.pow(2 - elapsedTimeRate, pow)));
    }
}
