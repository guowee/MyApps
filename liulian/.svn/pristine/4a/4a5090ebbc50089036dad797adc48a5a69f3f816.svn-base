package com.haomee.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 自定义图片控件，可加边框
 * @author zengxiaotao
 */
public class BorderImageView extends ImageView {

    private int mBorderColor;

    private int mBorderwidth;

    private float mBorderRadius;

    public BorderImageView(Context context) {
        super(context);
    }

    public BorderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BorderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画边框
        LayoutParams params = this.getLayoutParams();
        RectF rect = new RectF(0f, 0f, params.width, params.height);
        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(mBorderColor);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(mBorderwidth);
        canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, paint);
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public int getBorderwidth() {
        return mBorderwidth;
    }

    public void setBorderwidth(int borderwidth) {
        mBorderwidth = borderwidth;
    }

    public float getBorderRadius() {
        return mBorderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        mBorderRadius = borderRadius;
    }

}
