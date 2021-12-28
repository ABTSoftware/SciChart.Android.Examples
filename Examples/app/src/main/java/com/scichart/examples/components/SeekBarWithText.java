//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeekBarWithText.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SeekBarWithText extends androidx.appcompat.widget.AppCompatSeekBar {

    private final TextPaint textPaint = new TextPaint();

    public SeekBarWithText(Context context) {
        this(context, null);
    }

    public SeekBarWithText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public SeekBarWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, displayMetrics));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final String progressText = String.valueOf(getProgress());
        final int width = Math.round(Layout.getDesiredWidth(progressText, textPaint));
        final StaticLayout staticLayout= new StaticLayout(progressText, textPaint, width, Layout.Alignment.ALIGN_CENTER, 1f, 1f, false);

        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int seekBarAvailableWidth = getWidth() - leftPadding - rightPadding;
        final float progressRatio = (float) getProgress() / getMax();

        float thumbX = progressRatio * seekBarAvailableWidth + leftPadding;
        float thumbY = getHeight() / 2f - staticLayout.getHeight() / 2f;

        canvas.translate(thumbX, thumbY);

        staticLayout.draw(canvas);
    }
}
