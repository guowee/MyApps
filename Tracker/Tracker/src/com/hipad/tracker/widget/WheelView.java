package com.hipad.tracker.widget;

import java.util.Calendar;


import com.hipad.tracker.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
/**
 * 
 * @author guowei
 *
 */
public class WheelView extends View {
	// 圆心坐标
	private float mPointX = 100, mPointY = 100;
	// 旋转角度
	private int mAngle = 0;
	private int beginAngle = 0, currentAngle = 0;

	boolean isUp = false, isTouch = false;
	private OnWheelItemSelectListener mOnItemSelectListener;
	long beginTime, endTime;
	private Calendar now;
	private String TAG = "RotateView";

	private double gapangle = 0;
	int imageIndex = 0;

	public static Bitmap bitmapPointer;
	public static Bitmap bitmapPlate;

	public WheelView(Context context) {
		super(context);
		initWheelView();
	}

	public WheelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWheelView();
	}

	private void initWheelView() {

		bitmapPointer = BitmapFactory.decodeResource(getResources(),
				R.drawable.w_hand);// 中间不动的图片
		bitmapPlate = BitmapFactory.decodeResource(getResources(),
				R.drawable.weight_rule);// 随手指转动的图片
		
		 DisplayMetrics metric = getResources().getDisplayMetrics();
	     int width = metric.widthPixels;     // 屏幕宽度（像素）
	     
	     mPointX=width/2;
	     mPointY=bitmapPlate.getHeight()/2;

	}

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		switch (e.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			beginAngle = computeCurrentAngle(e.getX(), e.getY());
			isUp = false;
			now = Calendar.getInstance();
			beginTime = now.getTimeInMillis();
			// 如果点击触摸范围在圈外，则不处理
			if (getDistance(e.getX(), e.getY()) > bitmapPlate.getWidth() / 2) {
				isTouch = false;
			} else {
				isTouch = true;
			}
			return true;
		case MotionEvent.ACTION_MOVE:
			if (!isTouch) {
				return true;
			}
			currentAngle = computeCurrentAngle(e.getX(), e.getY());
			invalidate();
			return true;
		case MotionEvent.ACTION_UP:

			isUp = true;
			if (!isTouch) {
				return true;
			}
			now = Calendar.getInstance();
			endTime = now.getTimeInMillis();
			mAngle = mAngle / 4;
			gapangle = gapangle - mAngle;
			if (gapangle < 0) {
				gapangle = gapangle + 90;
			}
			if (gapangle > 90) {
				gapangle = gapangle - 90;
			}
			mOnItemSelectListener.onWheelItemSelected((int) gapangle);
			invalidate();
			gapangle = 0;
			return true;
		default:
			break;
		}
		return false;
	}

	// 获取距离圆心的距离
	private float getDistance(float x, float y) {
		// 根据圆心坐标计算角度
		float distance = (float) Math
				.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
						* (y - mPointY)));
		return distance;
	}

	// 子控件位置改变重新计算角度
	private int computeCurrentAngle(float x, float y) {
		// 根据圆心坐标计算角度
		float distance = (float) Math
				.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
						* (y - mPointY)));
		int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
		if (y < mPointY) {
			degree = -degree;
		}
		if (degree < 0) {
			degree += 360;
		}

		Log.i("RoundSpinView", "x:" + x + ",y:" + y + ",degree:" + degree);
		return degree;
	}

	@Override
	public void onDraw(Canvas canvas) {
		mAngle = currentAngle - beginAngle;
		Log.i("Bitmap", "bitmap:" + bitmapPlate);
		Bitmap tempBig = adjustPhotoRotation(bitmapPlate, mAngle);
		drawInCenter(canvas, tempBig, mPointX, mPointY, TAG);
		// 小圆(中间的圆心)
		drawInCenter(canvas, bitmapPointer, mPointX, mPointY, TAG);
	}

	public Bitmap adjustPhotoRotation(Bitmap bm, final float orientationDegree) {
		if (orientationDegree == 0) {
			return bm;
		}
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		try {

			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;

		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}
		return null;

	}

	private void drawInCenter(Canvas canvas, Bitmap bitmap, float left,
			float top, String text) {
		canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2,
				top - bitmap.getHeight() / 2, null);
	}

	public interface OnWheelItemSelectListener {
		public void onWheelItemSelected(int mode);
	}

	public void setOnWheelItemSelectedListener(
			OnWheelItemSelectListener listener) {
		mOnItemSelectListener = listener;
	}

}
