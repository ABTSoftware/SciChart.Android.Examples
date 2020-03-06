//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VitalSignsIndicatorsProvider.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.vitalSignsMonitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class VitalSignsIndicatorsProvider {
    private final Random random = new Random();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static final String[] BPM_VALUES = new String[] {"67", "69", "72", "74"};

    private static final String[] BP_VALUES = new String[] {"120/70", "115/70", "115/75", "120/75"};
    private static final int[] BPB_VALUES = new int[] {3, 4, 5};

    private static final String[] BV_VALUES = new String[] {"13.1", "13.2", "13.3", "13.0"};
    private static final int[] BVB_VALUES = new int[] {8, 9, 10};

    private static final String[] BO_VALUES = new String[] { "93", "95", "96", "97"};

    private String bpmValue = BPM_VALUES[0];

    private String bpValue= BP_VALUES[0];
    private int bpbValue = BPB_VALUES[0];

    private String bvValue = BV_VALUES[0];
    private int bvBar1Value = BVB_VALUES[0];
    private int bvBar2Value = BVB_VALUES[0];

    private String spoValue = BO_VALUES[0];
    private String spoClockValue = getTimeString();

    public String getBpmValue() {
        return bpmValue;
    }

    public String getBpValue() {
        return bpValue;
    }

    public int getBpbValue() {
        return bpbValue;
    }

    public String getBvValue() {
        return bvValue;
    }

    public int getBvBar1Value() {
        return bvBar1Value;
    }

    public int getBvBar2Value() {
        return bvBar2Value;
    }

    public String getSpoValue() {
        return spoValue;
    }

    public String getSpoClockValue() {
        return spoClockValue;
    }

    public void update() {
        bpmValue = randomString(BPM_VALUES);

        bpValue = randomString(BP_VALUES);
        bpbValue = randomInt(BPB_VALUES);

        bvValue = randomString(BV_VALUES);
        bvBar1Value = randomInt(BVB_VALUES);
        bvBar2Value = randomInt(BVB_VALUES);

        spoValue = randomString(BO_VALUES);
        spoClockValue = getTimeString();
    }

    private String randomString(String[] values){
        return values[random.nextInt(values.length)];
    }

    private int randomInt(int[] values){
        return values[random.nextInt(values.length)];
    }

    private String getTimeString() {
        return timeFormat.format(Calendar.getInstance().getTime());
    }
}
