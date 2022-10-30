package com.zds.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BarChart extends View {
    private List<BarData> data;

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, List<BarData> data_) {
        super(context);
        this.data = data_;
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
        canvas.drawRect(oval, paint);

    }

}

