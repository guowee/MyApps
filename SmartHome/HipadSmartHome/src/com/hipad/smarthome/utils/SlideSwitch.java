package com.hipad.smarthome.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.hipad.smarthome.R;
import com.hipad.smarthome.SignupActivity;

public class SlideSwitch extends View implements OnTouchListener {

	private Bitmap bg_on, bg_off, slide_img;
	/**
	 * 按下时的x和当前的x
	 */
	private float downX, nowX;

	/**
	 * 记录用户是否在滑动
	 */
	private boolean onSlip = false;

	/**
	 * 当前的状态
	 */
	public boolean nowStatus = false;

	/**
	 * 监听接口
	 */
	private OnChangedListener listener;

	public SlideSwitch(Context context, int on_bg_id, int off_bg_id, int imgID) {
		super(context);
		// TODO Auto-generated constructor stub
		bg_on = BitmapFactory.decodeResource(getResources(), on_bg_id);
		bg_off = BitmapFactory.decodeResource(getResources(), off_bg_id);
		slide_img = BitmapFactory.decodeResource(getResources(), imgID);
		setOnTouchListener(this);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = 0;

		if (onSlip) {// 是否是在滑动状态,
			if (nowX >= bg_on.getWidth())// 是否划出指定范围,不能让滑块跑到外头,必须做这个判断
				x = bg_on.getWidth() - slide_img.getWidth() / 2;// 减去滑块1/2的长度
			else
				x = nowX - slide_img.getWidth() / 2;
		} else {
			if (nowStatus) {// 根据当前的状态设置滑块的x值
				x = bg_on.getWidth() - slide_img.getWidth();
			} else {
				x = 0;
			}
		}

		// 对滑块滑动进行异常处理，不能让滑块出界
		if (x < 0) {
			x = 0;
		} else if (x > bg_on.getWidth() - slide_img.getWidth()) {
			x = bg_on.getWidth() - slide_img.getWidth();
		}

		// 根据nowX设置背景，开或者关状态
		if (nowX < bg_on.getWidth() - slide_img.getWidth()) {
			canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
		} else{
			canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景
		}

//		context.showInfoLog("onDraw x = "+x+", nowX = "+nowX+", bg_on.getWidth() = "+bg_on.getWidth());
		// 画出滑块
		canvas.drawBitmap(slide_img, x, 0, paint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (event.getX() > bg_off.getWidth()
					|| event.getY() > bg_off.getHeight()) {
				return false;
			} else {
				onSlip = true;
				downX = event.getX();
				nowX = downX;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			nowX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			onSlip = false;
			if (event.getX() >= (bg_on.getWidth() / 2)) {
				nowStatus = true;
				nowX = bg_on.getWidth() - slide_img.getWidth();
			} else {
				nowStatus = false;
				nowX = 0;
			}

			if (listener != null) {
				listener.OnChanged(SlideSwitch.this, nowStatus);
			}
			break;
		}
		}
//		context.showInfoLog("onTouch nowX = "+nowX);
		// 刷新界面
		invalidate();
		return true;
	}

	/**
	 * 为WiperSwitch设置一个监听，供外部调用的方法
	 * 
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}

	/**
	 * 设置滑动开关的初始状态，供外部调用
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		if (checked) {
			nowX = bg_off.getWidth();
		} else {
			nowX = 0;
		}
		nowStatus = checked;
	}

	/**
	 * 回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface OnChangedListener {
		public void OnChanged(SlideSwitch slideSwitch, boolean checkState);
	}
}
