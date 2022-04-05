//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StepProgressBar.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;

public class StepProgressBar extends View {

    private final Paint paint = new Paint();
    private int progressColor = ColorUtil.Green;
    private int progressBackgroundColor = ColorUtil.Transparent;
    private int progress = 0;
    private int max = 10;
    private boolean isVertical = false;
    private float spacing = 1f;
    private float barSize = 10f;

    public StepProgressBar(Context context) {
        super(context);

        paint.setStyle(Paint.Style.FILL);
    }

    public StepProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    public StepProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StepProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        paint.setStyle(Paint.Style.FILL);

        final TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StepProgressBar, defStyleAttr, defStyleRes);
        try {
            progressColor = typedArray.getColor(R.styleable.StepProgressBar_progressColor, ColorUtil.Green);
            progressBackgroundColor = typedArray.getColor(R.styleable.StepProgressBar_progressBackgroundColor, ColorUtil.Transparent);
            progress = typedArray.getInt(R.styleable.StepProgressBar_progress, 0);
            max = typedArray.getInt(R.styleable.StepProgressBar_max, 0);
            isVertical = typedArray.getBoolean(R.styleable.StepProgressBar_isVertical, false);
            spacing = typedArray.getDimension(R.styleable.StepProgressBar_spacing, 1f);
            barSize = typedArray.getDimension(R.styleable.StepProgressBar_barSize, 10f);
        } finally {
            typedArray.recycle();
        }
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public int getProgressBackgroundColor() {
        return progressBackgroundColor;
    }

    public void setProgressBackgroundColor(int progressBackgroundColor) {
        this.progressBackgroundColor = progressBackgroundColor;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        requestLayout();
        invalidate();
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
        requestLayout();
        invalidate();
    }

    public float getSpacing() {
        return spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        requestLayout();
        invalidate();
    }

    public float getBarSize() {
        return barSize;
    }

    public void setBarSize(float barSize) {
        this.barSize = barSize;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int requiredSize = (int) (max * barSize + spacing * (max - 1));
        final int width, height;
        if(isVertical) {
            width = View.resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 0);
            height = requiredSize;
        } else {
            width = requiredSize;
            height = View.resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(canvas == null) return;

        final float step = barSize + spacing;

        final int progress = this.progress;
        final int maxMinusProgress = this.max - progress;
        if(isVertical) {
            final float width = getWidth();
            float position = getHeight();

            paint.setColor(progressColor);
            for (int i = 0; i < progress; i++) {
                canvas.drawRect(0f, position - barSize, width, position, paint);

                position -= step;
            }

            paint.setColor(progressBackgroundColor);
            for (int i = 0; i < maxMinusProgress; i++) {
                canvas.drawRect(0f, position - barSize, width, position, paint);

                position -= step;
            }
        } else {
            float position = 0f;

            final int height = getHeight();

            paint.setColor(progressColor);
            for (int i = 0; i < progress; i++) {
                canvas.drawRect(position, 0f, position + barSize, height, paint);

                position += step;
            }

            paint.setColor(progressBackgroundColor);
            for (int i = 0; i < maxMinusProgress; i++) {
                canvas.drawRect(position, 0f, position + barSize, height, paint);

                position += step;
            }
        }

    }
}
