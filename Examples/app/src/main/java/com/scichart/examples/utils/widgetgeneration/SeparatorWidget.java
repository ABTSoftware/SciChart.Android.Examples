//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeparatorWidget.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils.widgetgeneration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scichart.examples.R;

public class SeparatorWidget extends WidgetBase {

    private SeparatorWidget() {
        super(0, null);
    }

    @Override
    public View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.example_toolbar_item, null, false);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        imageView.setImageResource(R.drawable.example_toolbar_separator);

        layout.addView(imageView);

        return layout;
    }

    public static class Builder {
        public SeparatorWidget build() {
            return new SeparatorWidget();
        }
    }
}
