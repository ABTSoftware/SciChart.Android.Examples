//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSingleChartBaseFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.base;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.examples.databinding.ExampleSingleChartFragmentBinding;

import org.jetbrains.annotations.NotNull;

public abstract class ExampleSingleChartBaseFragment extends ExampleBaseFragment<ExampleSingleChartFragmentBinding> {
    @NonNull
    @Override
    protected ExampleSingleChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSingleChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSingleChartFragmentBinding binding) {
        initExample(binding.surface);
    }

    protected abstract void initExample(@NotNull SciChartSurface surface);
}
