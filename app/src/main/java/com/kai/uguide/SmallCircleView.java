package com.kai.uguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class SmallCircleView extends View {

    int[] colors = new int[]{0xFF33B5E5,
            0xFFAA66CC,
            0xFF99CC00,
            0xFFFFBB33,
            0xFFFF4444,
            0xFF0099CC,
            0xFFAA66CC,
            0xFF669900,
            0xFFFF8800,
            0xFFCC0000};
    EmbossMaskFilter emboss;
    EmbossMaskFilter forBig;
    private Paint smallCircleCore;
    private Paint lineCore;
    private Paint bigArc;
    private Paint bigArcDraw;
    private Paint pencil;
    private Paint middleCircleBody;
    private Paint arcTouchedBack;
    private Paint circlePaint;
    private int initial, midInitial;
    private int finalangle;
    private int middleCircleRadius;
    private int i = 0;
    private int centerCircleradius;
    private RectF oval = new RectF();
    private RectF middleOval = new RectF();
    private RectF finalOVal = new RectF();
    private Context context;
    private int px, py;
    private boolean isClicked = true;
    private boolean[] arcTouched = new boolean[]{false, false};
    private ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>();

    public SmallCircleView(Context context) {
        super(context);
        this.context = context;
        initSmallCircle();

        // TODO Auto-generated constructor stub
    }

    public SmallCircleView(Context context, AttributeSet attri) {
        super(context, attri);
        this.context = context;

        initSmallCircle();
    }

    public SmallCircleView(Context context, AttributeSet attri, int defaultStyle) {
        super(context, attri, defaultStyle);
        this.context = context;

        initSmallCircle();
    }

    private void initSmallCircle() {
        setFocusable(true);

        smallCircleCore = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineCore = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        pencil = new Paint(Paint.ANTI_ALIAS_FLAG);

        lineCore.setColor(0xFFFFFFFF);
        lineCore.setStyle(Paint.Style.FILL);
        lineCore.setStrokeWidth(4);
        //bigCircleCore.setStrokeWidth(5);

        smallCircleCore.setStyle(Paint.Style.FILL);
        smallCircleCore.setColor(0xFFFFFFFF);

        bigArc.setColor(0xFF424242);
        bigArc.setStyle(Paint.Style.FILL);

        bigArcDraw = new Paint(smallCircleCore);
        bigArcDraw.setStrokeWidth(4);
        bigArcDraw.setColor(0xFF000000);

        pencil.setStrokeWidth(0.5f);
        pencil.setColor(0x80444444);
        pencil.setStyle(Paint.Style.STROKE);

        middleCircleBody = new Paint(bigArc);
        middleCircleBody.setColor(0xFFE6E6E6);

        arcTouchedBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcTouchedBack.setColor(0xFF81DAF5);
        arcTouchedBack.setStyle(Paint.Style.FILL);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(10);

        float[] direection = new float[]{1, 1, 1};
        float light = 0.4f;
        float specualr = 6;
        float blur = 3.5f;

        emboss = new EmbossMaskFilter(direection, light, specualr, blur);
        forBig = new EmbossMaskFilter(direection, 1f, 4, 2.5f);

        bitmap.clear();
        bitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo_f));
        bitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo_f));

        middleCircleBody.setMaskFilter(forBig);
        bigArc.setMaskFilter(forBig);

        String log = "";
        Log.d(log, "Value of px & py " + getMeasuredWidth() + " " + getMeasuredHeight());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        px = getMeasuredWidth();
        py = 0;

        initial = 144;
        finalangle = 252;

        centerCircleradius = 30;
        middleCircleRadius = 140;

        int init, fina;
        init = 90;
        fina = 180;
        finalOVal.set(px - middleCircleRadius - 4, py - middleCircleRadius - 4, px + middleCircleRadius + 4, py + middleCircleRadius + 4);
        middleOval.set(px - middleCircleRadius, py - middleCircleRadius, px + middleCircleRadius, py + middleCircleRadius);
        while (init < fina) {
            circlePaint.setColor(colors[i]);
            canvas.drawArc(finalOVal, init, 10, false, circlePaint);
            i++;
            if (i >= colors.length) {
                i = 0;
            }
            init = init + 10;

        }

        //Setting Mask Filter
        canvas.drawArc(middleOval, 90, 90, false, pencil);

        midInitial = 90;

        i = 0;

        //Creating the first Arc
        if (arcTouched[0]) {
            canvas.drawArc(middleOval, midInitial, 45, true, arcTouchedBack);
            canvas.drawArc(middleOval, midInitial, 45, true, pencil);
        } else {
            canvas.drawArc(middleOval, midInitial, 45, true, middleCircleBody);
            canvas.drawArc(middleOval, midInitial, 45, true, pencil);
        }
        canvas.drawBitmap(bitmap.get(0), null, (putBitmapTo(midInitial, 45, 140, px, py)), null);
        midInitial += 45;

        if (arcTouched[1]) {

            canvas.drawArc(middleOval, midInitial, 45, true, arcTouchedBack);
            canvas.drawArc(middleOval, midInitial, 45, true, pencil);
        } else {
            canvas.drawArc(middleOval, midInitial, 45, true, middleCircleBody);
            canvas.drawArc(middleOval, midInitial, 45, true, pencil);
        }
        canvas.drawBitmap(bitmap.get(1), null, (putBitmapTo(midInitial, 45, 140, px, py)), null);

        canvas.drawCircle(px, py - 10, 40, pencil);
        canvas.drawCircle(px, py - 10, 39, smallCircleCore);

        canvas.drawCircle(px, py - 10, 35, bigArc);
        canvas.drawCircle(px, py - 10, 20, smallCircleCore);

        canvas.drawCircle(px, py - 10, 15, bigArc);
        canvas.drawLine(px - 8, py - 10, px + 8, py - 10, lineCore);

        canvas.save();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        float centerX = px;
        float centerY = (py - centerCircleradius + (centerCircleradius / 4));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isInArc(90, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {
                    makeAllFalse();
                    arcTouched[0] = true;
                    invalidate();
                    return true;
                }

                if (isInArc(135, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {

                    makeAllFalse();
                    arcTouched[1] = true;
                    invalidate();
                    return true;
                }
            }
            case MotionEvent.ACTION_UP: {
                if (isInArc(90, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {
                    Toast.makeText(getContext(), "In the first Arc !!!", Toast.LENGTH_SHORT).show();
                    makeAllFalse();

                    invalidate();
                    return false;
                }

                if (isInArc(135, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {
                    Toast.makeText(getContext(), "In Second Arc", Toast.LENGTH_SHORT).show();
                    makeAllFalse();

                    invalidate();
                    return false;
                } else {
                    makeAllFalse();
                    invalidate();
                    return super.onTouchEvent(event);
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (isInArc(90, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {

                    makeAllFalse();
                    arcTouched[0] = true;
                    invalidate();
                    return false;
                }

                if (isInArc(135, 45, middleCircleRadius, px, py, event.getX(), event.getY())) {

                    makeAllFalse();
                    arcTouched[1] = true;
                    invalidate();
                    return false;
                } else {
                    makeAllFalse();
                    invalidate();
                    return super.onTouchEvent(event);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isInSideCenterCircle(float centerX, float centerY, float radius, float co_ordinateX, float co_ordinateY) {
        return ((Math.pow((centerX - co_ordinateX), 2)) + (Math.pow((centerY - co_ordinateY), 2)) - Math.pow(radius, 2)) <= 0;
    }

    private void changeIsClicked() {
        isClicked = (isClicked ? (false) : (true));
    }

    private RectF putBitmapTo(int startAngle, int SweepAngle, float radius, float centerX, float centerY) {
        float locx = (float) (centerX + ((radius / 17 * 11) * Math.cos(Math.toRadians(
                (startAngle + SweepAngle + startAngle) / 2
        ))
        ));
        float locy = (float) (centerY + ((radius / 17 * 11) * Math.sin(Math.toRadians(
                (startAngle + SweepAngle + startAngle) / 2
        ))
        ));

        return new RectF(locx - 20, locy - 20, locx + 20, locy + 20);

    }

    private boolean isInArc(int startAngle, int SweepAngle, float radius, float centerX, float centerY, float toCheckX, float toCheckY) {

        double degree;

        if ((Math.pow(centerX - toCheckX, 2) +
                Math.pow(centerY - toCheckY, 2) -
                Math.pow(radius, 2)) <= 0) {
            double radian = Math.atan((centerY - toCheckY) / (centerX - toCheckX));
            if (radian < 0) {
                degree = Math.toDegrees(radian);
                degree = 180 + degree;
            } else {
                degree = Math.toDegrees(radian);
            }
            if (degree >= startAngle) {
                if (degree <= (startAngle + SweepAngle)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private void makeAllFalse() {
        for (int i = 0; i < arcTouched.length; i++) {
            arcTouched[i] = false;
        }
    }
}