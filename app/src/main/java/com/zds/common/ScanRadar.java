package com.zds.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ScanRadar extends View {


    public ScanRadar(Context context) {

        super(context);

    }

    public ScanRadar(Context context,  AttributeSet attrs) {
        super(context, attrs);

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

        int leftForText = 200;
        float radius = 0;
        float lineLength = 100;
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        //透明度
        paint.setAlpha(80);
        paint.setStrokeWidth(2);
        float x = getMeasuredWidth();
        radius = x / 2 - leftForText;
        float centerPointX = x / 2;
        float centerPointY = x / 2;

        RectF oval = new RectF(leftForText, leftForText, getRight() - leftForText,x - leftForText);
        System.out.println(getRight());
        System.out.println(x);
        canvas.drawArc(oval,200,10,true,paint);

        float linePoint[] = {(float) (centerPointX + radius * Math.cos((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointY + radius * Math.sin((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointX + (radius + lineLength) * Math.cos((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointY + (radius + lineLength) * Math.sin((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointX + (radius + lineLength) * Math.cos((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointY + (radius + lineLength) * Math.sin((200.0 + 10 / 2)/180*3.14)),
                             (float) (centerPointX + (radius + lineLength) * Math.cos((200.0 + 10 / 2)/180*3.14) - 100),
                             (float) (centerPointY + (radius + lineLength) * Math.sin((200.0 + 10 / 2)/180*3.14))};



        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        canvas.drawLines(linePoint, paint);

        paint.setStrokeWidth(5);
        paint.setTextSize(50);

        canvas.drawText("aaa", (float) (centerPointX + (radius + lineLength) * Math.cos((200.0 + 10 / 2)/180*3.14) - 100),
                (float) (centerPointY + (radius + lineLength) * Math.sin((200.0 + 10 / 2)/180*3.14)), paint);

        paint.setColor(Color.RED);
        canvas.drawArc(oval,20,10,true,paint);




    }

}

