package com.haomee.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CornerImageView extends ImageView{
	

	private final Paint mBitmapPaint = new Paint();
	private Paint coverPaint = new Paint();
	private int mDrawableRadius = 10;
	

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	
	
	public CornerImageView(Context context) {
		super(context);
	}

	public CornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);

		if(this.getDrawable() instanceof BitmapDrawable){
			BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
			if (bd != null) {
				mBitmap = bd.getBitmap();
				
				//float scale = this.getWidth()*1.0f/mBitmap.getWidth();
				//mBitmap = zoomBitmap(mBitmap, scale);
				mBitmap = zoomBitmap(mBitmap, this.getWidth(), this.getHeight());
			}
			
			if(mBitmap!=null){
				drawRound(canvas);
			}
		}else{
			super.onDraw(canvas);
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
		RectF rect_top=new RectF();
		rect_top.left=0; 
		rect_top.top=0; 
		rect_top.right=width; 
		rect_top.bottom=height;  

		/*Rect rect_bottom=new Rect(); 
		rect_bottom.left=0;  
		rect_bottom.top=mDrawableRadius; 
		rect_bottom.right=width;    
		rect_bottom.bottom=height;  */
	    
		canvas.drawRoundRect(rect_top, mDrawableRadius, mDrawableRadius, mBitmapPaint);
		
		
		//canvas.drawRect(rect_bottom, mBitmapPaint);

		
		//is_rounded = true;

	}
	


}
