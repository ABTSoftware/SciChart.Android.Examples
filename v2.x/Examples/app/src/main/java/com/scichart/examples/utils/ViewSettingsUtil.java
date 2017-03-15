//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ViewSettingsUtil.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.SwitchCompat;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.scichart.charting.modifiers.IChartModifier;

public class ViewSettingsUtil {

    public static Dialog createSettingsPopup(Activity activity, @LayoutRes int id) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(id);

        return dialog;
    }

    public static void setUpSwitchCompat(Dialog dialog, @IdRes int checkBoxId, boolean isChecked, CompoundButton.OnCheckedChangeListener listener) {
        final SwitchCompat checkBox = (SwitchCompat) dialog.findViewById(checkBoxId);
        checkBox.setChecked(isChecked);
        checkBox.setOnCheckedChangeListener(listener);
    }

    public static void setUpRadioButton(Dialog dialog, @IdRes int checkBoxId, final IChartModifier modifier) {
        final RadioButton radioButton = (RadioButton) dialog.findViewById(checkBoxId);
        radioButton.setChecked(modifier.getIsEnabled());
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modifier.setIsEnabled(isChecked);
            }
        });
    }

    public static void setUpSeekBar(Dialog dialog, @IdRes int seekBarId, int progress, SeekBar.OnSeekBarChangeListener listener) {
        final SeekBar seekBar = (SeekBar) dialog.findViewById(seekBarId);
        seekBar.setOnSeekBarChangeListener(listener);
        seekBar.setProgress(progress);
    }
}