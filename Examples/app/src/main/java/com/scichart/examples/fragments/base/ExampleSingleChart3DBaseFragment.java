//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSingleChart3DBaseFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.examples.databinding.ExampleSingleChart3dFragmentBinding;

public abstract class ExampleSingleChart3DBaseFragment extends ExampleBaseFragment<ExampleSingleChart3dFragmentBinding> {
    @Override
    protected ExampleSingleChart3dFragmentBinding inflateBinding(LayoutInflater inflater) {
        return ExampleSingleChart3dFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSingleChart3dFragmentBinding binding) {
        initExample(binding.surface3d);
    }

    protected abstract void initExample(SciChartSurface3D surface3d);
}
