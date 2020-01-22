//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AudioData.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.audioAnalyzer;

import com.scichart.core.model.LongValues;
import com.scichart.core.model.ShortValues;

public class AudioData {
    public final int pointsCount;
    public final LongValues xData;
    public final ShortValues yData;

    public AudioData(int pointsCount) {
        this.pointsCount = pointsCount;
        this.xData = new LongValues(pointsCount);
        this.yData = new ShortValues(pointsCount);

        this.xData.setSize(pointsCount);
        this.yData.setSize(pointsCount);
    }
}
