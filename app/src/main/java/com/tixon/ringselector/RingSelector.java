package com.tixon.ringselector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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

    private int pressedSegmentNumber;
    private boolean shouldDrawSector = false;

    private Paint segmentPaint, sectorPaint;
    private Path sectorPath;
    private RectF innerOval, externalOval;

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
        innerOval = new RectF();
        externalOval = new RectF();

        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setColor(Color.parseColor("#cacaca"));
        segmentPaint.setStyle(Paint.Style.STROKE);
        Log.d("myLogs", "segmentStrokeWidth (4dp) pixel size = " + getResources().getDimensionPixelSize(R.dimen.segment_stroke_width));
        segmentPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.segment_stroke_width));

        sectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sectorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        sectorPaint.setColor(Color.parseColor("#770000bb"));
        //sectorPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.segment_stroke_width));
        sectorPaint.setStrokeWidth(1.0f);

        sectorPath = new Path();
        sectorPath.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSegments(canvas);
        drawSelectedSector(canvas);
    }

    private float coordinateX(int segmentCount, float radius) {
        return x0 + radius * (float)Math.cos(Math.toRadians((float)segmentCount*ratio()));
    }

    private float coordinateY(int segmentCount, float radius) {
        return y0 + radius * (float)Math.sin(Math.toRadians((float)segmentCount*ratio()));
    }

    private void drawSegments(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        x0 = (float)width/2.0f;
        y0 = (float)height/2.0f;
        int size = width > height? height: width;
        radiusExt = (float)size/2.5f;
        radiusIn = radiusExt/1.4f;

        innerOval.set(x0-radiusIn, y0-radiusIn, x0+radiusIn, y0+radiusIn);
        externalOval.set(x0-radiusExt, y0-radiusExt, x0+radiusExt, y0+radiusExt);

        for(int i = 0; i < SEGMENTS_COUNT; i++) {
            float xExt = coordinateX(i, radiusExt);
            float yExt = coordinateY(i, radiusExt);
            float xIn = coordinateX(i, radiusIn);
            float yIn = coordinateY(i, radiusIn);
            canvas.drawLine(xIn, yIn, xExt, yExt, segmentPaint);
        }
    }

    private float ratio() {
        return 360.0f/(float)SEGMENTS_COUNT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        float x, y, radius;
        switch(action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                radius = (float)Math.sqrt((x-x0)*(x-x0) + (y-y0)*(y-y0));
                Log.d("myLogs", "x = " + x + ", y = " + y + ", radius = " + radius);
                draw(x, y, radius);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                radius = (float)Math.sqrt((x-x0)*(x-x0) + (y-y0)*(y-y0));
                Log.d("myLogs", "Moving: x = " + x + ", y = " + y + ", radius = " + radius);
                draw(x, y, radius);
                break;
            default: break;
        }
        return true;
    }

    private void draw(float x, float y, float radius) {
        if(radius >= radiusIn && radius <= radiusExt) {
            float cos = (scalarProduct(x, y, x0+radius, y0))/(radius*radius);
            float degree = (float)Math.toDegrees(Math.acos(cos));
            if(y > y0) degree = 360 - degree;
            Log.w("myLogs", "touch: X = "+x+", Y = "+y+
                    "; deg = "+(degree+ratio()/2)%360+", segment = "+segmentNumber(degree));
            pressedSegmentNumber = segmentNumber(degree);
            shouldDrawSector = true;
            invalidate();
        }
    }

    private int segmentNumber(float degree) {
        float degreeWithSegmentWidthOffset = (degree + ratio()/2) % 360;
        return (int) (degreeWithSegmentWidthOffset / ratio());
    }

    private float scalarProduct(float x1, float y1, float x2, float y2) {
        return (x1-x0)*(x2-x0) + (y1-y0)*(y2-y0);
    }

    private int numberNext(int number) {
        return ((number+4))%60;
    }

    private int numberPrev(int number) {
        int result = ((number-4))%60;
        if(result < 0) result += 60;
        return result;
    }

    private void drawSelectedSector(Canvas canvas) {
        if(!shouldDrawSector) return;

        int numberNext = numberNext(SEGMENTS_COUNT-pressedSegmentNumber);
        int numberPrev = numberPrev(SEGMENTS_COUNT-pressedSegmentNumber);
        if(Math.abs(numberNext-numberPrev)>8)
            if(numberNext>numberPrev) numberNext = (60 - numberNext)*-1;
            else numberPrev = (60 - numberPrev)*-1;
        float degreeFrom = numberPrev*ratio();
        float degreeTo = numberNext*ratio();
        Log.e("myLogs", "prev="+numberPrev+", next="+numberNext+", from="+degreeFrom+", to="+degreeTo);
        float degreeSweep = Math.abs(degreeTo-degreeFrom);

        sectorPath.reset();
        sectorPath.moveTo(coordinateX(numberPrev, radiusIn), coordinateY(numberPrev, radiusIn));

        sectorPath.arcTo(innerOval, degreeFrom, degreeSweep);
        sectorPath.lineTo(coordinateX(numberNext, radiusExt), coordinateY(numberNext, radiusExt));
        sectorPath.arcTo(externalOval, degreeTo, -degreeSweep);
        sectorPath.lineTo(coordinateX(numberPrev, radiusIn), coordinateY(numberPrev, radiusIn));

        canvas.drawPath(sectorPath, sectorPaint);
    }
}
