package com.example.shiwu.customviewstudy.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.shiwu.customviewstudy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiwu on 2017/8/19.
 */

public class PieChartView extends View {
    private List<DrawData> datas;
    private Paint mPaint;
    private Resources resources;

    private float totalNum;//总数量

    private float currentAngle;//当前画到的角度

    private int circleRadius;//大圆半径
    private int ringWidth;//圆环宽度
    private int textSize;//描述文字字体大小

    private RectF circleRect;

    private int centerX;
    private int centerY;

    private List<Point> middlePoints;
    private List<Point> turnPoints;

    private int textMargin;


    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pieChartStyle, defStyleAttr, 0);
        ringWidth = (int) ta.getDimension(R.styleable.pieChartStyle_ringWidth, 150);
        textSize =(int) ta.getDimension(R.styleable.pieChartStyle_describeTextSize, sp2px(context,12));
        ta.recycle();
        resources = context.getResources();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        textMargin = dp2px(context,10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas == null) {
            return;
        }
        centerX = getWidth()/ 2;
        centerY = getHeight()/ 2;
        circleRadius = (getHeight() -getPaddingTop() - getPaddingBottom())/ 2;
        currentAngle = -90.0f;
        totalNum = 0;

        for (DrawData data : datas) {
            totalNum += data.getRate();
        }

        Bitmap source = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas sourceCanvas = new Canvas(source);

        circleRect = new RectF(centerX - circleRadius, centerY - circleRadius, centerX + circleRadius, centerY + circleRadius);
        if (middlePoints != null) {
            middlePoints.clear();
        }
        for (DrawData data : datas) {
            drawArc(sourceCanvas, data);
        }
        drawTextAndLine(canvas);
        canvas.drawBitmap(source, 0, 0, mPaint);
    }

    private void drawTextAndLine(Canvas canvas) {
        int leftHeight = 0;
        int rightHeight = 0;
        List<Point> left = new ArrayList<>();
        List<Point> right = new ArrayList<>();
        for (Point point : middlePoints) {
            int quadrant = getQuadrant(point);
            switch (quadrant) {
                case 1:
                case 2:
                    right.add(point);
                    break;
                case 3:
                case 4:
                    left.add(point);
                    break;
            }
        }
        mPaint.setTextSize(textSize);
        mPaint.setColor(getContext().getResources().getColor(R.color.black));
        int startX,startY,endX,endY;
        startX = centerX + circleRadius + dp2px(getContext(),10);
        Path path = new Path();
        for (int i = 0; i < right.size(); i++) {
            String title = datas.get(i).getName();
            Rect titleRect = getTitleSize(title);
            Point turnPoint = turnPoints.get(i);
            RectF textRect = new RectF();
            endX = startX + titleRect.width();
            startY = (int) (turnPoint.y - (titleRect.height() * 1.0f)/2);
            endY = startY + titleRect.height();
            if (startY > rightHeight + textMargin) {
                rightHeight = endY;
            } else {
                startY = rightHeight + textMargin;
                endY = rightHeight + textMargin + titleRect.height();
                rightHeight += (titleRect.height() + textMargin);
            }
            path.moveTo(right.get(i).x, right.get(i).y);
            path.lineTo(turnPoint.x, turnPoint.y);
            path.lineTo(startX -dp2px(getContext(),10),turnPoint.y);
            path.lineTo(startX,startY + (titleRect.height() * 1.0f)/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, mPaint);
            textRect.set(startX,startY,endX,endY);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(title, textRect.left, textRect.bottom, mPaint);
        }
        for (int i = left.size() - 1; i >=0; i--) {
            String title = datas.get(right.size() + i).getName();
            Rect titleRect = getTitleSize(title);
            Point turnPoint = turnPoints.get(right.size() + i);
            RectF textRect = new RectF();
            startX = centerX - circleRadius -dp2px(getContext(),10);
            endX = startX;
            startX -= titleRect.width();
            startY = (int) (turnPoint.y - (titleRect.height() * 1.0f)/2);
            endY = startY + titleRect.height();
            if (startY > leftHeight + textMargin) {
                leftHeight = endY;
            } else {
                startY = leftHeight + textMargin;
                endY = leftHeight + textMargin + titleRect.height();
                leftHeight += titleRect.height() + textMargin;
            }
            path.moveTo(left.get(i).x, left.get(i).y);
            path.lineTo(turnPoint.x, turnPoint.y);
            path.lineTo(endX+dp2px(getContext(),10),turnPoint.y);
            path.lineTo(endX,startY + (titleRect.height() * 1.0f)/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, mPaint);
            textRect.set(startX,startY,endX,endY);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(title, textRect.left, textRect.bottom, mPaint);
        }
    }

    private void drawArc(Canvas canvas, DrawData data) {
        mPaint.setColor(resources.getColor(data.getColor()));
        float sweepAngle = data.getRate() / totalNum * 360;
        if (datas.indexOf(data) == datas.size() - 1)//最后一项直接画满剩余角度
            sweepAngle = 270 - currentAngle;
        canvas.drawArc(circleRect, currentAngle, sweepAngle, true, mPaint);

        Point middlePoint = getPoint(currentAngle, sweepAngle, false);
        Point turnPoint = getPoint(currentAngle, sweepAngle, true);
        if (middlePoints == null) {
            middlePoints = new ArrayList<>();
        }
        middlePoints.add(middlePoint);

        if (turnPoints == null) {
            turnPoints = new ArrayList<>();
        }
        turnPoints.add(turnPoint);

        //折线path
        Path path = new Path();
        path.moveTo(middlePoint.x, middlePoint.y);
        path.lineTo(turnPoint.x, turnPoint.y);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(path, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        currentAngle += sweepAngle;
    }

    private Rect getTitleSize(String str) {
        //获取文字的长度
        Rect rect = new Rect();
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect;
    }

    //获取折线起点或者转折点
    private Point getPoint(float currentAngle, float sweepAngle, boolean isTurningPoint) {
        float circleX = getWidth() / 2;
        float circleY = getHeight() / 2;

        float middleAngle = currentAngle + sweepAngle / 2;
        int middleX;
        int middleY;
        double radian = middleAngle * 2 * Math.PI / 360;
        if (!isTurningPoint) {
            middleX = (int) (circleX + Math.cos(radian) * (circleRadius - dp2px(getContext(),10)));
            middleY = (int) (circleY + Math.sin(radian) * (circleRadius - dp2px(getContext(),10)));
        } else {
            middleX = (int) (circleX + Math.cos(radian) * (circleRadius + dp2px(getContext(),10)));
            middleY = (int) (circleY + Math.sin(radian) * (circleRadius + dp2px(getContext(),10)));
        }

        return new Point(middleX, middleY);
    }

    //判断圆弧中点属于第几象限，确定折线的方向以及文字的位置
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
    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
