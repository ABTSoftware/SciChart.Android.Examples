//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DefaultVitalSignsDataProvider.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.vitalSignsMonitor;

import android.content.Context;
import android.util.Log;

import com.scichart.core.model.DoubleValues;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.SciListUtil;
import com.scichart.examples.fragments.base.DataProviderBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.parseDouble;

public class DefaultVitalSignsDataProvider extends DataProviderBase<VitalSignsData> {
    //1. Heart rate or pulse rate (ECG HR)
    //2. Blood Pressure (NI BP)
    //3. Blood Volume (SV ml)
    //4. Blood Oxygenation (SPo2)
    private static final String ECG_TRACES = "data/EcgTraces.csv";
    private static final float SAMPLE_RATE = 800f;

    private int currentIndex = 0;
    private int totalIndex = 0;
    private boolean isATrace = false;

    private final DoubleValues xValues = new DoubleValues();
    private final DoubleValues ecgHeartRate= new DoubleValues();
    private final DoubleValues bloodPressure = new DoubleValues();
    private final DoubleValues bloodVolume = new DoubleValues();
    private final DoubleValues bloodOxygenation = new DoubleValues();

    public DefaultVitalSignsDataProvider(Context context) {
        super(1000L, TimeUnit.MICROSECONDS);

        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(ECG_TRACES)));

            String line = reader.readLine();
            while(line != null) {
                final String[] split = line.split(",");
                xValues.add(parseDouble(split[0]));
                ecgHeartRate.add(parseDouble(split[1]));
                bloodPressure.add(parseDouble(split[2]));
                bloodVolume.add(parseDouble(split[3]));
                bloodOxygenation.add(parseDouble(split[4]));

                line = reader.readLine();
            }

        } catch (Exception e){
            Log.e("Load ECG", "Failed to load ECG", e);
        }
    }

    @Override
    protected VitalSignsData onNext() {
        if(currentIndex >= xValues.size()) {
            currentIndex = 0;
        }

        final float time = totalIndex / SAMPLE_RATE % 10;
        final double ecgHeartRate = this.ecgHeartRate.get(currentIndex);
        final double bloodPressure = this.bloodPressure.get(currentIndex);
        final double bloodVolume = this.bloodVolume.get(currentIndex);
        final double bloodOxygenation = this.bloodOxygenation.get(currentIndex);

        final VitalSignsData data = new VitalSignsData(time, ecgHeartRate, bloodPressure, bloodVolume, bloodOxygenation, isATrace);

        currentIndex++;
        totalIndex++;

        if(totalIndex % 8000 == 0) {
            isATrace = !isATrace;
        }
        return data;
    }

    public final DoubleRange getEcgHeartRateRange() {
        return getMinMaxRange(ecgHeartRate);
    }

    public final DoubleRange getBloodPressureRange() {
        return getMinMaxRange(bloodPressure);
    }

    public final DoubleRange getBloodVolumeRange() {
        return getMinMaxRange(bloodVolume);
    }

    public final DoubleRange getBloodOxygenationRange() {
        return getMinMaxRange(bloodOxygenation);
    }

    private static DoubleRange getMinMaxRange(DoubleValues values) {
        final DoubleRange range = new DoubleRange();
        SciListUtil.instance().minMax(values.getItemsArray(), 0, values.size(), range);

        range.growBy(0.1, 0.1);
        return range;
    }
}
