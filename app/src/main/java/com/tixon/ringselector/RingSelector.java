package com.tixon.ringselector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tikhon.osipov on 06.12.2016
 */

public class RingSelector extends View {
    private static final int SEGMENTS_COUNT = 60;

    float radiusIn, radiusExt;
    float x0, y0;

    private Paint segmentPaint;

    public RingSelector(Context context) {
        super(context);
        init();
    }

    public RingSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) @SuppressWarnings("unused")
    public RingSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setColor(Color.parseColor("#cacaca"));
        segmentPaint.setStyle(Paint.Style.STROKE);
        Log.d("myLogs", "segmentStrokeWidth (4dp) pixel size = " + getResources().getDimensionPixelSize(R.dimen.segment_stroke_width));
        segmentPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.segment_stroke_width));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSegments(canvas);
    }

    private void drawSegments(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        x0 = (float)width/2.0f;
        y0 = (float)height/2.0f;
        int size = width > height? height: width;
        radiusExt = (float)size/2.0f;
        radiusIn = radiusExt/1.5f;

        float ratio = 360.0f/(float)SEGMENTS_COUNT;

        for(int i = 0; i < SEGMENTS_COUNT; i++) {
            float xExt = x0 + radiusExt * (float)Math.cos(Math.toRadians((float)i*ratio));
            float yExt = y0 + radiusExt * (float)Math.sin(Math.toRadians((float)i*ratio));
            float xIn = x0 + radiusIn * (float)Math.cos(Math.toRadians((float)i*ratio));
            float yIn = y0 + radiusIn * (float)Math.sin(Math.toRadians((float)i*ratio));
            canvas.drawLine(xIn, yIn, xExt, yExt, segmentPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float radius = (float)Math.sqrt((x-x0)*(x-x0) + (y-y0)*(y-y0));
            Log.d("myLogs", "x = " + x + ", y = " + y + ", radius = " + radius);
            if(radius >= radiusIn && radius <= radiusExt) {
                Log.w("myLogs", "touch: X = " + event.getX() + ", Y = " + event.getY());
            }
        }
        return super.onTouchEvent(event);
    }
}
