package com.example.shiwu.customviewstudy.prectise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.shiwu.customviewstudy.R;

/**
 * Created by shiwu on 2017/7/28.
 */

public class ShaderView extends View {
    public ShaderView(Context context) {
        this(context,null);
    }

    public ShaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Shader shader = new LinearGradient(0,0,getWidth(),getHeight(), Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
        paint.setShader(shader);
//        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
//        canvas.drawCircle(getWidth()/2,getHeight()/2,Math.min(getWidth()/2,getHeight()/2),paint);

        Shader secondShader = new RadialGradient(width/2,height/2,Math.min(width/2,height/2),Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
        paint.setShader(secondShader);
//        canvas.drawCircle(width/2,height/2,Math.min(width/2,height/2),paint);

        Shader sweepShader = new SweepGradient(width/2,height/2,Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"));

        paint.setShader(sweepShader);
//        canvas.drawCircle(width/2,height/2,Math.min(width/2,height/2),paint);
//        canvas.drawRect(0,0,getWidth(),getHeight(),paint);

        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.source_img);
        BitmapShader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float scale = getScale(source);
        Matrix matrix = new Matrix();
        matrix.setScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
//        paint.setShader(bitmapShader);
        Bitmap dis = BitmapFactory.decodeResource(getResources(),R.drawable.dis_img);
        Shader childShader = new BitmapShader(dis, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale2 = getScale(dis);
        Matrix matrix1 = new Matrix();
        matrix1.setScale(scale2,scale2);
        childShader.setLocalMatrix(matrix1);
        Shader composeShader = new ComposeShader(bitmapShader,childShader, PorterDuff.Mode.SRC_OVER);
        paint.setShader(bitmapShader);
        canvas.drawCircle(width/2,height/2,Math.min(width/2,height/2),paint);

    }

    private float getScale(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        return Math.min((float) getWidth()*1.0f/bitmapWidth,(float) getHeight()*1.0f/bitmapHeight);
    }
}
