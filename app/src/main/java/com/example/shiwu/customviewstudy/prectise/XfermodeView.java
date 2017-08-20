package com.example.shiwu.customviewstudy.prectise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.example.shiwu.customviewstudy.R;

/**
 * Created by shiwu on 2017/7/28.
 */

public class XfermodeView extends View {
    public XfermodeView(Context context) {
        super(context);
    }

    public XfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XfermodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        int width = getWidth();
        int height = getHeight();

        Paint paint = new Paint();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas drawCanvas = new Canvas(bitmap);


        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.source_img);
        Bitmap dis = getShape();

        float sourceScale = getScale(source);

        Matrix sourceMatrix = new Matrix();
        sourceMatrix.setScale(sourceScale,sourceScale);

        Bitmap newSource = Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),sourceMatrix,false);

        drawCanvas.drawBitmap(newSource,getWidth()/2 - newSource.getWidth()/2,getHeight()/2 - newSource.getHeight()/2,paint);

        paint.setXfermode(xfermode);
        drawCanvas.drawBitmap(dis,0,0,paint);

        paint.setXfermode(null);

        canvas.drawBitmap(bitmap,0,0,null);
    }

    private float getScale(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        return Math.min((float) getWidth()*1.0f/bitmapWidth,(float) getHeight()*1.0f/bitmapHeight);
    }

    private Bitmap getShape(){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(getWidth()/2,getHeight()/2,Math.min(getWidth()/2,getHeight()/2),paint);
        return bitmap;
    }
}
