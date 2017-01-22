package com.haomee.view;

import com.haomee.util.ViewUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class Arrow extends View {
	
	private Canvas myCanvas;
	public Point start, end;
	private Paint paint;
	
	private int screen_width, screen_height;
	private int arrow_start_width;

	public Arrow(Context context) {
		super(context);
		init(context);
	}

	public Arrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Arrow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private void init(Context context){
		
		arrow_start_width = ViewUtil.dip2px(context, 30);
		
		ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
		
		paint = new Paint();
		paint.setColor(Color.parseColor("#22c4af"));
		//paint.setStrokeWidth(4);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width =  dm.widthPixels;
		screen_height =  dm.heightPixels;
	}
	
	public boolean isOutOfScreen(){
		return start.x<0 || start.x>screen_width || start.y<0 || start.y>screen_height
				|| end.x<(start.x+10) || end.x>screen_width || end.y<0 || end.y>screen_height;
	}
	

	public void setStart(Point p) {
		this.start = p;
		invalidate();
	}

	public void setEnd(Point p) {
		this.end = p;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.myCanvas=canvas;

		if (start != null && end != null) {
			//canvas.drawLine(start.x, start.y, end.x, end.y, paint);
			
			/*boolean is_out = isOutOfScreen();
			
			if(!status_out_off && is_out && listener!=null){
				listener.onOutOffScreen();
			}
			
			status_out_off = is_out;
			
			if(!is_out){
				drawAL2(start.x, start.y, end.x, end.y);
			}*/
			
			drawAL2(start.x, start.y, end.x, end.y);
			
		}

	}
	
	/**
	 * 画箭头
	 */
	public void drawAL2(int sx, int sy, int ex, int ey) {
		Path triangle = new Path();
		triangle.moveTo(sx, sy-arrow_start_width/2);
		triangle.lineTo(ex, ey);
		triangle.lineTo(sx, sy+arrow_start_width/2);
		triangle.close();
		myCanvas.drawPath(triangle, paint);
	}

	/*private boolean status_out_off = false;
	private ArrowStatusListener listener;
	public void setArrowStatusListener(ArrowStatusListener listener){
		this.listener = listener;
	}

	interface ArrowStatusListener{
		public void onOutOffScreen();
	}*/
}
