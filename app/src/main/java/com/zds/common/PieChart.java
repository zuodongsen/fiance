package com.zds.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
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
            System.out.println(data.get(loop).pieValue);
            if(data.get(loop).pieValue / this.pieValueSum < 0.01) {
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
            it.pieStringDown = String.valueOf(it.pieValue);
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
        float centerPointX = standardX + width / 2;
        float centerPointY = standardY + width / 2;
        float arcRate = (float) (3.14 / 180);
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
        float radius = 0;
        float lineLength = 100;
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
        radius = x / 2 - leftForText;
        float centerPointX = x / 2;
        float centerPointY = x / 2;

        RectF oval = new RectF(leftForText, leftForText, getRight() - leftForText,x - leftForText);
        System.out.println(getRight());

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

