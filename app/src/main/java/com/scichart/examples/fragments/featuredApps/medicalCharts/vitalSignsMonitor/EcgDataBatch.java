//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// EcgDataBatch.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor;

import com.scichart.core.model.DoubleValues;

import java.util.List;

public class EcgDataBatch {
    public final DoubleValues xValues = new DoubleValues();

    public final DoubleValues ecgHeartRateValuesA = new DoubleValues();
    public final DoubleValues bloodPressureValuesA = new DoubleValues();
    public final DoubleValues bloodVolumeValuesA = new DoubleValues();
    public final DoubleValues bloodOxygenationA = new DoubleValues();

    public final DoubleValues ecgHeartRateValuesB = new DoubleValues();
    public final DoubleValues bloodPressureValuesB = new DoubleValues();
    public final DoubleValues bloodVolumeValuesB = new DoubleValues();
    public final DoubleValues bloodOxygenationB = new DoubleValues();

    public VitalSignsData lastVitalSignsData;

    public final void updateData(List<VitalSignsData> vitalSignsDataList) {
        xValues.clear();
        ecgHeartRateValuesA.clear();
        ecgHeartRateValuesB.clear();
        bloodPressureValuesA.clear();
        bloodPressureValuesB.clear();
        bloodVolumeValuesA.clear();
        bloodVolumeValuesB.clear();
        bloodOxygenationA.clear();
        bloodOxygenationB.clear();

        final int size = vitalSignsDataList.size();
        for (int i = 0; i < size; i++) {
            final VitalSignsData vitalSignsData = vitalSignsDataList.get(i);

            xValues.add(vitalSignsData.xValue);

            if (vitalSignsData.isATrace) {
                ecgHeartRateValuesA.add(vitalSignsData.ecgHeartRate);
                bloodPressureValuesA.add(vitalSignsData.bloodPressure);
                bloodVolumeValuesA.add(vitalSignsData.bloodVolume);
                bloodOxygenationA.add(vitalSignsData.bloodOxygenation);

                ecgHeartRateValuesB.add(Double.NaN);
                bloodPressureValuesB.add(Double.NaN);
                bloodVolumeValuesB.add(Double.NaN);
                bloodOxygenationB.add(Double.NaN);
            } else {
                ecgHeartRateValuesB.add(vitalSignsData.ecgHeartRate);
                bloodPressureValuesB.add(vitalSignsData.bloodPressure);
                bloodVolumeValuesB.add(vitalSignsData.bloodVolume);
                bloodOxygenationB.add(vitalSignsData.bloodOxygenation);

                ecgHeartRateValuesA.add(Double.NaN);
                bloodPressureValuesA.add(Double.NaN);
                bloodVolumeValuesA.add(Double.NaN);
                bloodOxygenationA.add(Double.NaN);
            }

        }

        lastVitalSignsData = vitalSignsDataList.get(size - 1);
    }
}
