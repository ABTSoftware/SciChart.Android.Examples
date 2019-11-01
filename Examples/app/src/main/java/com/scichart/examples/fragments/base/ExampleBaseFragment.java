//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleBaseFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.base;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scichart.examples.utils.widgetgeneration.Widget;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.extensions3d.builders.SciChart3DBuilder;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public abstract class ExampleBaseFragment extends Fragment {

    protected final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();
    protected final SciChart3DBuilder sciChart3DBuilder = SciChart3DBuilder.instance();

    public boolean showDefaultModifiersInToolbar() {
        return true;
    }

    public List<Widget> getToolbarItems() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), null);
        ButterKnife.bind(this, root);
        initExample();

        return root;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initExample();
}