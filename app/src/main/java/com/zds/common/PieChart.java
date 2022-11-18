package com.zds.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PieChart extends View {
    private List<PieData> data;

    private final int COLOR_MIN = 1;
    private final int COLOR_MAX = 16777216;
    private final int COLOR_RANGE = COLOR_MAX - COLOR_MIN;

    public float pieValueSum;

    float centerPointX;
    float centerPointY;
    float radius;

    final float arcRate = (float) (3.14 / 180);

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, List<PieData> data_) {
        super(context);
        this.data = data_;
        genaratePidAngle();

    }

    public PieData getDataByPoint(float x, float y) {
        if(Math.pow(x - this.centerPointX, 2) + Math.pow(y - this.centerPointY, 2) > Math.pow(this.radius, 2)) {
            return null;
        }
        float angle = this.getAngleByPoint(x, y);
        return this.getDataByAngle(angle);
    }

    private float getAngleByPoint(float x, float y) {
        if(x == this.centerPointX) {
            return (y > this.centerPointY ? 90 : 270);
        }
        float angle = (float) Math.atan((y - this.centerPointX) / (x - this.centerPointX)) / arcRate;

        if(y >= this.centerPointY) {
            if(x < this.centerPointX) {
                return angle + 180;
            }else {
                return angle;
            }
        }else {
            if(x < this.centerPointX) {
                return angle + 180;
            }else {
                return angle + 360;
            }
        }
    }

    private PieData getDataByAngle(float angle) {
        for(PieData d : this.data) {
            if(d.pieAngleStart < angle && angle - d.pieAngleStart <= d.pieAngleSwap) {
                return d;
            }
        }
        return null;
    }



    private float calcValueSun() {
        float rst = 0;
        for (PieData it: data) {
            rst += it.pieValue;
        }
        return rst;
    }

    private void genaratePidAngle() {
        this.pieValueSum = this.calcValueSun();
        data.sort(new Comparator<PieData>() {
            @Override
            public int compare(PieData o1, PieData o2) {
                if(o1.pieValue > o2.pieValue)
                    return 1;
                else
                    return -1;
            }
        });

        for(int loop = 1; loop < data.size();) {
            if(data.get(loop).pieValue / this.pieValueSum < 0.01) {
                data.get(0).pieStringPoly.add(data.get(loop).pieString);
                if(loop == 1)
                    data.get(0).pieString = "杂项";
                data.get(0).pieValue += data.get(loop).pieValue;
                data.remove(loop);
            }else
                break;
        }

        for(int loop = 0; loop < data.size() / 2;) {
            Collections.swap(data, loop, data.size() - 1 - loop);
            loop += 2;
        }

        float degreeValue = (float) (360.0 / this.pieValueSum);
        float degreeSum = 0;
        for (PieData it: data) {
            float pieAngle = it.pieValue * degreeValue;
            it.pieAngleStart = degreeSum;
            it.pieAngleSwap = pieAngle;
            it.pieColor = this.genarateColor();
            it.pieStringDown = String.format("%.1f", it.pieValue);
            degreeSum += pieAngle;
        }

    }

    private int genarateColor() {
        double rand = Math.random();
        double rst = (rand * this.COLOR_RANGE + this.COLOR_MIN);
        String hex = "#" + Integer.toHexString(-(int)rst);
        return Color.parseColor(hex);
    }

    private void genarateLinePoint(float standardX, float standardY, float radius, float width, int textHeight) {
        this.centerPointX = standardX + width / 2;
        this.centerPointY = standardY + width / 2;

        float lineLength = 100;

        for (PieData it: data) {
            float lineAngle = (it.pieAngleStart + it.pieAngleSwap / 2) * arcRate;
            it.linePoint[0] = (float) (centerPointX + radius * Math.cos(lineAngle));
            it.linePoint[1] = (float) (centerPointY + radius * Math.sin(lineAngle));
            it.linePoint[2] = (float) (centerPointX + (radius + lineLength) * Math.cos(lineAngle));
            it.linePoint[3] = (float) (centerPointY + (radius + lineLength) * Math.sin(lineAngle));

            it.linePoint[4] = (float) (centerPointX + (radius + lineLength) * Math.cos(lineAngle));
            it.linePoint[5] = (float) (centerPointY + (radius + lineLength) * Math.sin(lineAngle));
            if(it.linePoint[2] > it.linePoint[0]) {
                it.linePoint[6] = it.linePoint[4] + lineLength;
                it.textPoint[0] = it.linePoint[4];
            } else {
                it.linePoint[6] = it.linePoint[4] - lineLength;
                it.textPoint[0] = it.linePoint[6];
            }

            it.linePoint[7] = it.linePoint[5];
            it.textPoint[1] = it.linePoint[5] - 8;
            it.textPoint[2] = it.linePoint[5] + textHeight;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 这是绘图方法
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        int leftForText = 250;
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        //透明度
        paint.setAlpha(80);
        paint.setStrokeWidth(4);
        paint.setTextSize(30);

        String temp = "Happy"; //被获取的文字
        Rect rect = new Rect();
        paint.getTextBounds(temp, 0, temp.length(), rect);
        int height = rect.height();

        float x = getMeasuredWidth();
        this.radius = x / 2 - leftForText;

        RectF oval = new RectF(leftForText, leftForText, getRight() - leftForText,x - leftForText);

        this.genarateLinePoint(0, 0, radius, x, height);

        for(PieData it : this.data) {
            paint.setColor(it.pieColor);
            canvas.drawArc(oval, it.pieAngleStart, it.pieAngleSwap,true, paint);

            paint.setColor(Color.BLACK);
            canvas.drawLines(it.linePoint, paint);
            canvas.drawText(it.pieString, it.textPoint[0], it.textPoint[1], paint);
            canvas.drawText(it.pieStringDown, it.textPoint[0], it.textPoint[2], paint);
        }

    }

}

