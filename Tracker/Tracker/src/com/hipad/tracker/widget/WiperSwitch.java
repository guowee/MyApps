package com.hipad.tracker.widget;

import com.hipad.tracker.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author guowei
 *
 */
public class WiperSwitch extends View {

	/** 设置滑动的图片 */
	private Bitmap bgOn, bgOff, slideButtonBG;
	/** 设置开关的状态，打开/关闭。 默认：关闭 */
	private boolean currentState = false;
	/** 当前滑动块的移动距离 */
	private int currentX;
	/** 记录当前滑动块滑动的状态。默认，false */
	private boolean isSliding = false;
	/** 开关状态改变监听 */
	private OnToggleStateChangeListener mListener;

	public WiperSwitch(Context context) {
		super(context);
		init();
	}

	public WiperSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 载入图片资源
		bgOn = BitmapFactory.decodeResource(getResources(),
				R.drawable.swich_on_background);
		bgOff = BitmapFactory.decodeResource(getResources(),
				R.drawable.swich_off_background);
		slideButtonBG = BitmapFactory.decodeResource(getResources(),
				R.drawable.swich_action);
	}

	/**
	 * 移动效果的处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 手指按下
			currentX = (int) event.getX();
			isSliding = true;
			break;
		case MotionEvent.ACTION_MOVE: // 手指移动
			currentX = (int) event.getX();
			break;
		case MotionEvent.ACTION_UP: // 手指抬起
			isSliding = false;
			// 判断当前滑动块，偏向于哪一边，如果滑动块的中心点<背景的中心点，设置为关闭状态
			int bgCenter = bgOn.getWidth() / 2;
			boolean state = currentX >= bgCenter; // 改变后的状态
			// 手指抬起时，回调监听，返回当前的开关状态
			if (state != currentState && mListener != null) {
				mListener.onToggleStateChange(state);
			}
			currentState = state;
			break;
		default:
			break;
		}
		invalidate(); // 刷新控件，该方法会调用onDraw(Canvas canvas)方法
		return true; // 自己处理事件，不让父类负责消耗事件
	}

	/**
	 * 测量当前控件宽高时回调
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 设置开关的宽和高
		setMeasuredDimension(bgOn.getWidth(), bgOn.getHeight());
	}

	/**
	 * 绘制控件
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 1,滑动开关背景绘制到控件

		if (currentX < (bgOn.getWidth() / 2)) {
			canvas.drawBitmap(bgOff, 0, 0, null);
		} else {
			canvas.drawBitmap(bgOn, 0, 0, null);// 画出打开时的背景
		}

		// 2，绘制滑动块显示的位置，开启或关闭
		if (isSliding) {
			int left = currentX - slideButtonBG.getWidth() / 2; // 处理手指触点，将触点从slidingButton的左边移动到中间
			if (left < 0) {
				left = 0;
			} else if (left > bgOn.getWidth() - slideButtonBG.getWidth()) {
				left = bgOn.getWidth() - slideButtonBG.getWidth();
			}
			canvas.drawBitmap(slideButtonBG, left, 0, null);
		} else {
			if (currentState) {
				canvas.drawBitmap(bgOn, 0, 0, null);
				// 绘制打开状态
				canvas.drawBitmap(slideButtonBG, bgOn.getWidth()
						- slideButtonBG.getWidth(), 0, null);
			} else {
				// 绘制关闭状态
				canvas.drawBitmap(bgOff, 0, 0, null);
				canvas.drawBitmap(slideButtonBG, 0, 0, null);
			}
		}
	}

	public void setToggleState(boolean b) {

		if (currentState != b) {
			currentState = b;
			invalidate();// 重画控件
		}

	}

	/**
	 * 对外设置监听方法
	 * 
	 * @param listener
	 */
	public void setOnToggleStateChangeListener(
			OnToggleStateChangeListener listener) {
		this.mListener = listener;
	}

	public interface OnToggleStateChangeListener {
		public void onToggleStateChange(boolean b);

	}

}