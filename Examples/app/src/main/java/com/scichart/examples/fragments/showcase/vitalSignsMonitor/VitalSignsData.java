//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VitalSignsData.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.vitalSignsMonitor;

public class VitalSignsData {
    public final double xValue;
    public final double ecgHeartRate;
    public final double bloodPressure;
    public final double bloodVolume;
    public final double bloodOxygenation;
    public final boolean isATrace;

    public VitalSignsData(double xValue, double ecgHeartRate, double bloodPressure, double bloodVolume, double bloodOxygenation, boolean isATrace) {
        this.xValue = xValue;
        this.ecgHeartRate = ecgHeartRate;
        this.bloodPressure = bloodPressure;
        this.bloodVolume = bloodVolume;
        this.bloodOxygenation = bloodOxygenation;
        this.isATrace = isATrace;
    }
}
