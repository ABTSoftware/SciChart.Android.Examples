//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2023. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DateLabelProvider.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.utils;

import com.scichart.charting.numerics.labelProviders.LabelProviderBase;
import com.scichart.charting.visuals.axes.IDateAxis;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.utility.ComparableUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class CustomDateLabelProvider extends LabelProviderBase<IDateAxis> {
    final SimpleDateFormat firstLastDateFormatter = new SimpleDateFormat("MMM dd\nHH:mm");
    final SimpleDateFormat otherDateFormatter = new SimpleDateFormat("HH:mm");

    public CustomDateLabelProvider() {
        super(IDateAxis.class);
    }


    @Override
    public CharSequence formatLabel(Comparable dataValue) {
        final double doubleValue = ComparableUtil.toDouble(dataValue);

        return formatLabel(doubleValue);
    }

    @Override
    public CharSequence formatCursorLabel(Comparable dataValue) {
        final double doubleValue = ComparableUtil.toDouble(dataValue);

        return formatCursorLabel(doubleValue);
    }

    @Override
    public CharSequence formatLabel(double dataValue) {
        return otherDateFormatter.format(dataValue);
    }

    @Override
    public CharSequence formatCursorLabel(double dataValue) {
        return formatLabel(dataValue);
    }

    @Override
    protected void updateTickLabels(List formattedTickLabels, DoubleValues majorTicks) {
        final int size = majorTicks.size();
        final double[] majorTicksArray = majorTicks.getItemsArray();

        for (int i = 0; i < size; i++) {
            final double valueToFormat = majorTicksArray[i];
            final CharSequence formattedValue;
            if (i == 0 || i == size - 1) {
                formattedValue = firstLastDateFormatter.format(valueToFormat);
            } else {
                formattedValue = formatLabel(valueToFormat);
            }
            formattedTickLabels.add(formattedValue);
        }
    }
}
