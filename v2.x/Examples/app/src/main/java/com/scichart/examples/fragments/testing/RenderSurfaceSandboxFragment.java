//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RenderSurfaceSandboxFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.scichart.drawing.common.ITexture2D;
import com.scichart.extensions.builders.FontStyleBuilder;
import com.scichart.core.IServiceContainer;
import com.scichart.core.ServiceContainer;
import com.scichart.core.utility.ViewGroupUtil;
import com.scichart.core.utility.messaging.EventAggregator;
import com.scichart.core.utility.messaging.IEventAggregator;
import com.scichart.drawing.canvas.RenderSurface;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.common.IAssetManager2D;
import com.scichart.drawing.common.IBrush2D;
import com.scichart.drawing.common.IFont;
import com.scichart.drawing.common.IPen2D;
import com.scichart.drawing.common.IRenderContext2D;
import com.scichart.drawing.common.IRenderSurface;
import com.scichart.drawing.common.IRenderSurfaceRenderer;
import com.scichart.drawing.common.LinearGradientBrushStyle;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.common.RadialGradientBrushStyle;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.TextureBrushStyle;
import com.scichart.drawing.common.TextureMappingMode;
import com.scichart.drawing.opengl.GLTextureView;
import com.scichart.drawing.opengl.RenderSurfaceGL;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnItemSelected;

import static com.scichart.extensions.builders.PenStyleBuilder.*;

public class RenderSurfaceSandboxFragment extends ExampleBaseFragment implements SeekBar.OnSeekBarChangeListener {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    private IRenderSurface renderSurface;

    private TestRenderSurfaceRenderer renderer;

    @Bind(R.id.renderSurfaceTypeSpinner)
    Spinner renderSurfaceTypeSpinner;

    @Bind(R.id.rendersurfaceFrame)
    FrameLayout renderSurfaceFrame;

    @Bind(R.id.rotation)
    SeekBar rotation;

    @Bind(R.id.translateX)
    SeekBar translateX;

    @Bind(R.id.translateY)
    SeekBar translateY;

    @Override
    protected int getLayoutId() {
        return R.layout.example_render_surface_sandbox_fragment;
    }

    @Override
    protected void initExample() {
        rotation.setOnSeekBarChangeListener(this);
        translateX.setOnSeekBarChangeListener(this);
        translateY.setOnSeekBarChangeListener(this);

        final SpinnerStringAdapter adapter = new SpinnerStringAdapter(getActivity(), R.array.render_surface_types);
        renderSurfaceTypeSpinner.setAdapter(adapter);
        renderSurfaceTypeSpinner.setSelection(2);

        final IServiceContainer services = new ServiceContainer();

        final EventAggregator eventAggregator = new EventAggregator();
        services.registerService(IEventAggregator.class, eventAggregator);


        final int width = 100;
        final int height = 200;
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawColor(ColorUtil.White);

        paint.setColor(ColorUtil.Green);
        canvas.drawRect(20, 10, width - 20, height - 10, paint);
        paint.setColor(ColorUtil.Red);
        canvas.drawRect(50, 20, width - 50, height - 20, paint);

        renderer = new TestRenderSurfaceRenderer(bitmap);

        future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                invalidateRS();
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        future.cancel(false);
    }

    @OnItemSelected(R.id.renderSurfaceTypeSpinner)
    public void onRenderSurfaceTypeSelected(int position) {
        final String itemAtPosition = (String) renderSurfaceTypeSpinner.getItemAtPosition(position);
        if (itemAtPosition.contains("Canvas")) {
           setRenderSurface(new RenderSurface(getActivity()));
        } else if (itemAtPosition.contains("OpenGL")) {
            setRenderSurface(new RenderSurfaceGL(getActivity()));
        } else if (itemAtPosition.contains("Texture")) {
            setRenderSurface(new GLTextureView(getActivity()));
        }
    }

    private void setRenderSurface(IRenderSurface renderSurface) {
        if(this.renderSurface == renderSurface) return;

        if (this.renderSurface != null) {
            this.renderSurface.setRenderer(null);
        }

        ViewGroupUtil.safeRemoveChild(renderSurfaceFrame, this.renderSurface);
        this.renderSurface = renderSurface;
        ViewGroupUtil.safeAddChild(renderSurfaceFrame, this.renderSurface);

        if (this.renderSurface != null) {
            this.renderSurface.setRenderer(this.renderer);
        }
    }

    private void invalidateRS() {
        if(renderSurface != null) {
            renderer.setTransform(rotation.getProgress(), translateX.getProgress(), translateY.getProgress());

            renderSurface.invalidateElement();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
         invalidateRS();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class TestRenderSurfaceRenderer implements IRenderSurfaceRenderer{
        private final RectF sprite2Rect = new RectF(0,0, 0.5f, 0.5f);
        private final RectF sprite3Rect = new RectF(0.25f,0.25f, 0.75f, 0.75f);

        private final BrushStyle solidStyle;
        private final BrushStyle linearGradient;
        private final BrushStyle radialGradient;
        private final BrushStyle textureStyle;

        private final PenStyle simpleLine;
        private final PenStyle aaLine;

        private final PenStyle dashedSimpleLine;
        private final PenStyle dashedAaLine;

        private final PenStyle thickSimpleLine;
        private final PenStyle thickAaLine;

        private final PenStyle dashedThickSimpleLine;
        private final PenStyle dashedThickAaLine;

        private final FontStyle fontStyle;
        private final FontStyle customFontStyle;

        private final Bitmap texture;

        private final float[] xAxisArrow = {0,0, 50, 0, 30, -10, 50, 0, 30, 10, 50, 0};
        private final float[] yAxisArrow = {0,0, 0, 50, -10, 30, 0, 50, 10, 30, 0, 50};

        private float degrees, dx, dy;

        public TestRenderSurfaceRenderer(Bitmap texture) {
            solidStyle = new SolidBrushStyle(ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8));
            linearGradient = new LinearGradientBrushStyle(0, 0, 1, 1, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
            radialGradient = new RadialGradientBrushStyle(0.5f, 0.5f, 0.5f, 0.5f, ColorUtil.argb(0xEE, 0xFF, 0xC9, 0xA8), ColorUtil.argb(0xEE, 0x13, 0x24, 0xA5));
            textureStyle = new TextureBrushStyle(texture);

            fontStyle = new FontStyleBuilder(getActivity()).withTextSize(32).withTextColor(ColorUtil.Red).build();
            customFontStyle = new FontStyleBuilder(getActivity()).withTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)).withTextSize(23).withTextColor(ColorUtil.Yellow).build();

            simpleLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Red).withAntiAliasing(false).withThickness(1f).build();
            aaLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Green).withAntiAliasing(true).withThickness(1f).build();

            dashedSimpleLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Blue).withAntiAliasing(false).withThickness(1f).withStrokeDashArray(new float[]{5, 10, 5, 10}).build();
            dashedAaLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Magenta).withAntiAliasing(true).withThickness(1f).withStrokeDashArray(new float[]{10, 5, 10, 5}).build();

            thickSimpleLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Red).withAntiAliasing(false).withThickness(10f).build();
            thickAaLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Green).withAntiAliasing(true).withThickness(10f).build();

            dashedThickSimpleLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Blue).withAntiAliasing(false).withThickness(20f).withStrokeDashArray(new float[]{5, 10, 5, 10}).build();
            dashedThickAaLine = new SolidPenStyleBuilder(getActivity()).withColor(ColorUtil.Magenta).withAntiAliasing(true).withThickness(20f).withStrokeDashArray(new float[]{0, 20, 10, 5}).build();

            this.texture = texture;
        }

        @Override
        public void onSurfaceAttached(IRenderSurface surface) {

        }

        @Override
        public void onSurfaceDetached(IRenderSurface surface) {

        }

        @Override
        public void onSurfaceSizeChanged(int width, int height, int oldWidth, int oldHeight) {

        }

        public void setTransform(float degrees, float dx, float dy){
            this.degrees = degrees;
            this.dx = dx  - translateX.getMax() / 2;
            this.dy = dy - translateY.getMax() / 2;
        }

        @Override
        public void onDraw(IRenderContext2D renderContext, IAssetManager2D assetManager) {
            renderContext.translate(dx, dy);
            renderContext.rotate(degrees);

            final IBrush2D solidBrushPerScreen = assetManager.createBrush(solidStyle, TextureMappingMode.PerScreen);
            final IBrush2D radialGradientBrushPerScreen = assetManager.createBrush(radialGradient, TextureMappingMode.PerScreen);
            final IBrush2D linearGradientBrushPerScreen = assetManager.createBrush(linearGradient, TextureMappingMode.PerScreen);
            final IBrush2D textureBrushPerScreen = assetManager.createBrush(textureStyle, TextureMappingMode.PerScreen);

            final IBrush2D solidBrushPerPrimitive = assetManager.createBrush(solidStyle, TextureMappingMode.PerPrimitive);
            final IBrush2D radialGradientBrushPerPrimitive = assetManager.createBrush(radialGradient, TextureMappingMode.PerPrimitive);
            final IBrush2D linearGradientBrushPerPrimitive = assetManager.createBrush(linearGradient, TextureMappingMode.PerPrimitive);
            final IBrush2D textureBrushPerPrimitive = assetManager.createBrush(textureStyle, TextureMappingMode.PerPrimitive);

            final IPen2D simpleLine = assetManager.createPen(this.simpleLine);
            final IPen2D aaLine = assetManager.createPen(this.aaLine);
            final IPen2D dashedSimpleLine = assetManager.createPen(this.dashedSimpleLine);
            final IPen2D dashedAaLine = assetManager.createPen(this.dashedAaLine);
            final IPen2D thickSimpleLine = assetManager.createPen(this.thickSimpleLine);
            final IPen2D thickAaLine = assetManager.createPen(this.thickAaLine);
            final IPen2D dashedThickSimpleLine = assetManager.createPen(this.dashedThickSimpleLine);
            final IPen2D dashedThickAaLine = assetManager.createPen(this.dashedThickAaLine);

            final IFont font = assetManager.createFont(this.fontStyle);
            final IFont customFont = assetManager.createFont(this.customFontStyle);

            final ITexture2D sprite1 = assetManager.createTexture(texture);
            final ITexture2D sprite2 = assetManager.createTexture(texture, sprite2Rect);
            final ITexture2D sprite3 = assetManager.createTexture(texture, sprite3Rect);

            renderContext.drawLines(xAxisArrow, 0, xAxisArrow.length, simpleLine);
            renderContext.drawLines(yAxisArrow, 0, yAxisArrow.length, aaLine);

            renderContext.save();

            renderContext.translate(10, 10);

            renderContext.drawLine(0, 0, 80, 80, simpleLine);
            renderContext.drawLine(100, 0, 180, 80, aaLine);
            renderContext.drawLine(200, 0, 280, 80, dashedSimpleLine);
            renderContext.drawLine(300, 0, 380, 80, dashedAaLine);

            renderContext.drawLine(0, 100, 80, 180, thickSimpleLine);
            renderContext.drawLine(100, 100, 180, 180, thickAaLine);
            renderContext.drawLine(200, 100, 280, 180, dashedThickSimpleLine);
            renderContext.drawLine(300, 100, 380, 180, dashedThickAaLine);

            renderContext.translate(0, 200);

            renderContext.drawRect(0, 0, 80, 80, simpleLine);
            renderContext.drawRect(100, 0, 180, 80, aaLine);
            renderContext.drawRect(200, 0, 280, 80, dashedSimpleLine);
            renderContext.drawRect(300, 0, 380, 80, dashedAaLine);

            renderContext.drawRect(0, 100, 80, 180, thickSimpleLine);
            renderContext.drawRect(100, 100, 180, 180, thickAaLine);
            renderContext.drawRect(200, 100, 280, 180, dashedThickSimpleLine);
            renderContext.drawRect(300, 100, 380, 180, dashedThickAaLine);

            renderContext.translate(0, 200);

            renderContext.fillRect(0, 0, 80, 80, solidBrushPerScreen);
            renderContext.fillRect(100, 0, 180, 80, linearGradientBrushPerScreen);
            renderContext.fillRect(200, 0, 280, 80, radialGradientBrushPerScreen);
            renderContext.fillRect(300, 0, 380, 80, textureBrushPerScreen);

            renderContext.fillRect(0, 100, 80, 180, solidBrushPerPrimitive);
            renderContext.fillRect(100, 100, 180, 180, linearGradientBrushPerPrimitive);
            renderContext.fillRect(200, 100, 280, 180, radialGradientBrushPerPrimitive);
            renderContext.fillRect(300, 100, 380, 180, textureBrushPerPrimitive);

            renderContext.translate(0, 200);

            renderContext.drawEllipse(50, 50, 80, 80, simpleLine, solidBrushPerScreen);
            renderContext.drawEllipse(150, 50, 80, 80, aaLine, linearGradientBrushPerScreen);
            renderContext.drawEllipse(250, 50, 80, 80, dashedSimpleLine, radialGradientBrushPerScreen);
            renderContext.drawEllipse(350, 50, 80, 80, dashedAaLine, textureBrushPerScreen);

            renderContext.drawEllipse(50, 150, 80, 80, thickSimpleLine, solidBrushPerPrimitive);
            renderContext.drawEllipse(150, 150, 80, 80, thickAaLine, linearGradientBrushPerPrimitive);
            renderContext.drawEllipse(250, 150, 80, 80, dashedThickSimpleLine, radialGradientBrushPerPrimitive);
            renderContext.drawEllipse(350, 150, 80, 80, dashedThickAaLine, textureBrushPerPrimitive);

            renderContext.restore();

            renderContext.save();

            renderContext.translate(500, 0);

            renderContext.drawText(font, 0, 0, fontStyle.textColor, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            renderContext.drawText(font, 0, 50, fontStyle.textColor, "abcdefghijklmnopqrstuvwxyz");
            renderContext.drawText(font, 0, 100, fontStyle.textColor, "1234567890~!@#$%^&*()-+=/|\\'\"");

            renderContext.drawText(customFont, 0, 150, customFontStyle.textColor, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            renderContext.drawText(customFont, 0, 200, customFontStyle.textColor, "abcdefghijklmnopqrstuvwxyz");
            renderContext.drawText(customFont, 0, 250, customFontStyle.textColor, "1234567890~!@#$%^&*()-+=/|\\'\"");

            renderContext.translate(0, 300);

            renderContext.drawSprite(sprite1, 0, 0);
            renderContext.translate(0, sprite1.getHeight() + 10);

            renderContext.drawSprite(sprite2, 0, 0);
            renderContext.translate(0, sprite2.getHeight() + 10);

            renderContext.drawSprite(sprite3, 0, 0);
            renderContext.translate(0, sprite3.getHeight() + 10);

            sprite1.dispose();
            sprite2.dispose();
            sprite3.dispose();
        }
    }
}
