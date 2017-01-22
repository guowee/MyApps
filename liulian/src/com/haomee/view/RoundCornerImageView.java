package com.haomee.view;

import com.haomee.liulian.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerImageView extends ImageView {

	private final Paint mBitmapPaint = new Paint();
	private Paint coverPaint = new Paint();
	private int mDrawableRadius = 10;

	private int bg_color;	// 背景
	
	private int mask_color;		// 蒙层

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;

	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView, defStyle, 0);

		mDrawableRadius = a.getDimensionPixelSize(R.styleable.RoundCornerImageView_radius, 10);
		bg_color = a.getColor(R.styleable.RoundCornerImageView_bg_color, 0);
		mask_color = a.getColor(R.styleable.RoundCornerImageView_mask_color, 0);
		
		a.recycle();
	}

	public void setBgColor(int bg_color) {
		this.bg_color = bg_color;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);

		if (bg_color != 0) {
			drawColor(canvas);
		}
		
		if (this.getDrawable()!=null && this.getDrawable() instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
			if (bd != null) {
				mBitmap = bd.getBitmap();
				mBitmap = zoomBitmap(mBitmap, this.getWidth(), this.getHeight());
			}

			if (mBitmap != null) {
				drawRound(canvas);
			}
		}
		

	}

	// 图片缩放
	private Bitmap zoomBitmap(Bitmap bitmap, float scale) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	// 图片缩放
	private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	//private boolean is_rounded = false;

	private void drawColor(Canvas canvas) {
		RectF rect = new RectF();
		rect.left = 0;
		rect.top = 0;
		rect.right = this.getWidth();
		rect.bottom = this.getHeight();
		coverPaint.setColor(bg_color);
		canvas.drawRoundRect(rect, mDrawableRadius, mDrawableRadius, coverPaint); // 画背景 
	}

	private void drawRound(Canvas canvas) {

		if (mBitmap == null) {
			return;
		}

		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		// 控制只上面显示圆角
		RectF rect_top = new RectF();
		rect_top.left = 0;
		rect_top.top = 0;
		rect_top.right = width;
		rect_top.bottom = height;

		/*Rect rect_bottom=new Rect(); 
		rect_bottom.left=0;  
		rect_bottom.top=mDrawableRadius; 
		rect_bottom.right=width;    
		rect_bottom.bottom=height;  */

		canvas.drawRoundRect(rect_top, mDrawableRadius, mDrawableRadius, mBitmapPaint); // 画图

		if(mask_color!=0){
			coverPaint.setColor(mask_color);
			canvas.drawRoundRect(rect_top, mDrawableRadius, mDrawableRadius, coverPaint); // 画蒙层
		}
		
		//canvas.drawRect(rect_bottom, mBitmapPaint);

		//is_rounded = true;

	}

}
