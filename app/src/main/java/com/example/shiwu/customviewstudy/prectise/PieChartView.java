package com.example.shiwu.customviewstudy.prectise;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.shiwu.customviewstudy.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by shiwu on 2017/8/19.
 */

public class PieChartView extends View {
    private List<DrawData> datas;
    private Paint mPaint;
    private Resources resources;

    private float totalNum;

    private float currentAngle;

    private int circleRadius = 250;
    private int ringWidth = 100;
    private int textSize;

    private RectF circleRect;

    private int centerX;
    private int centerY;


    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.pieChartStyle,defStyleAttr,0);
        circleRadius = (int) ta.getDimension(R.styleable.pieChartStyle_pieRadius, 250);
        ringWidth = (int) ta.getDimension(R.styleable.pieChartStyle_ringWidth, 150);
        textSize = (int) ta.getDimension(R.styleable.pieChartStyle_describeTextSize, 24);
        ta.recycle();
        resources = context.getResources();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas == null) {
            return;
        }
        Log.e("CircleView", "onDraw");
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        currentAngle = -90.0f;
        totalNum = 0;

        for (DrawData data : datas) {
            totalNum += data.getRate();
        }

        Bitmap source = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas sourceCanvas = new Canvas(source);

        circleRect = new RectF(getWidth() / 2 - circleRadius, getHeight() / 2 - circleRadius, getWidth() / 2 + circleRadius, getHeight() / 2 + circleRadius);

        for (DrawData data : datas) {
            drawItem(sourceCanvas, data);
        }

        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mPaint.setXfermode(xfermode);
        sourceCanvas.drawCircle(getWidth() / 2, getHeight() / 2, circleRadius - ringWidth, mPaint);
        mPaint.setXfermode(null);
        canvas.drawBitmap(source, 0, 0, mPaint);
    }

    private void drawItem(Canvas canvas, DrawData data) {
        mPaint.setColor(resources.getColor(data.getColor()));
        float sweepAngle = data.getRate() / totalNum * 360;
        String drawName = data.getName() + (new BigDecimal(data.getRate()).divide(new BigDecimal(totalNum / 100), 2, BigDecimal.ROUND_HALF_UP) + "%");
        if (datas.indexOf(data) == datas.size() - 1)
            sweepAngle = 270 - currentAngle;
        canvas.drawArc(circleRect, currentAngle, sweepAngle, true, mPaint);

        Point middlePoint = getPoint(currentAngle, sweepAngle, false);
        Point turnPoint = getPoint(currentAngle, sweepAngle, true);

        Path path = new Path();

        canvas.drawCircle(middlePoint.x, middlePoint.y, 5, mPaint);
        path.moveTo(middlePoint.x, middlePoint.y);
        path.lineTo(turnPoint.x, turnPoint.y);

        Rect rect = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(drawName, 0, drawName.length(), rect);

        int nameLength = rect.width();
        RectF textFect = new RectF();
        int quadrant = getQuadrant(middlePoint);
        Log.e("drawItem", "---quadrant = " + quadrant);
        switch (quadrant) {
            case 1:
                path.rLineTo(nameLength, 0);
                textFect.set(turnPoint.x, turnPoint.y - rect.height(), turnPoint.x + rect.width(), turnPoint.y);
                break;
            case 2:
                path.rLineTo(nameLength, 0);
                textFect.set(turnPoint.x, turnPoint.y, turnPoint.x + rect.width(), turnPoint.y + rect.height());
                break;
            case 3:
                path.rLineTo(-nameLength, 0);
                textFect.set(turnPoint.x - rect.width(), turnPoint.y, turnPoint.x, turnPoint.y + rect.height());
                break;
            case 4:
                path.rLineTo(-nameLength, 0);
                textFect.set(turnPoint.x - rect.width(), turnPoint.y - rect.height(), turnPoint.x, turnPoint.y);
                break;
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(path, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(drawName, textFect.left, textFect.bottom, mPaint);
        currentAngle += sweepAngle;
    }

    private Point getPoint(float currentAngle, float sweepAngle, boolean isTurningPoint) {
        float circleX = getWidth() / 2;
        float circleY = getHeight() / 2;

        float middleAngle = currentAngle + sweepAngle / 2;
        int middleX;
        int middleY;
        Log.e("getPoint", "----middleAngle = " + middleAngle);
        Log.e("getPoint", "----sin = " + Math.sin(middleAngle) + "---cos = " + Math.cos(middleAngle));
        double radian = middleAngle * 2 * Math.PI / 360;
        if (!isTurningPoint) {
            middleX = (int) (circleX + Math.cos(radian) * (circleRadius + 10));
            middleY = (int) (circleY + Math.sin(radian) * (circleRadius + 10));
        } else {
            middleX = (int) (circleX + Math.cos(radian) * (circleRadius + 40));
            middleY = (int) (circleY + Math.sin(radian) * (circleRadius + 40));
        }


        return new Point(middleX, middleY);
    }

    private int getQuadrant(Point point) {
        int x = point.x;
        int y = point.y;

        if (x >= centerX && y <= centerY)
            return 1;
        else if (x >= centerX && y > centerY)
            return 2;
        else if (x < centerX && y >= centerY)
            return 3;
        else
            return 4;


    }


    public void setData(List<DrawData> datas) {
        this.datas = datas;
        invalidate();
    }
}
