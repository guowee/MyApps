package com.hipad.tracker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class BatteryMeterView extends View {
	private int mPower = 10;
	public BatteryMeterView(Context context, AttributeSet attrs,int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	public BatteryMeterView(Context context) {
		super(context);
	}
	
	public BatteryMeterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);int battery_left = 0;
		int battery_top = 0;
		int battery_width = 40;
		int battery_height = 50;
		
		int battery_inside_margin = 0;
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD , Typeface.BOLD));
		
		Rect rect = new Rect(battery_left, battery_top, battery_left + battery_height, battery_top + battery_width);
		canvas.drawRect(rect, paint);
		
		float power_percent = mPower / 100.0f;
		
		Paint paint2 = new Paint(paint);
		paint2.setStyle(Style.FILL);
		paint2.setColor(Color.GREEN);
		if(power_percent != 0) {
			int p_left = battery_left + battery_inside_margin;
			int p_right=battery_left+battery_height-battery_inside_margin;
			int p_bottom = battery_top + battery_width - battery_inside_margin ;
			int p_top=p_bottom-battery_inside_margin-(int)((battery_width - battery_inside_margin) * power_percent);
			
			Rect rect2 = new Rect(p_left, p_top, p_right , p_bottom);
			canvas.drawRect(rect2, paint2);
			paint.setTextSize(20f);
			canvas.drawText(mPower+"%", p_left+battery_height/8, p_top+8*battery_inside_margin, paint);
		}
		
	}
	
	public void setPower(int power) {
		mPower = power;
		if(mPower < 0) {
			mPower = 0;
		}
		invalidate();
	}
	
}