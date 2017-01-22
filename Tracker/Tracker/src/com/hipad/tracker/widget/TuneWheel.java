package com.hipad.tracker.widget;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;
/**
 * 
 * @author guowei
 *
 */
public class TuneWheel extends View {

	public interface OnValueChangeListener {
		public void onValueChange(float value);
	}

	public static final int MOD_TYPE_ONE = 10;

	private static final int ITEM_ONE_DIVIDER = 10;

	private static final int ITEM_MAX_HEIGHT = 30;
	private static final int ITEM_MIN_HEIGHT = 15;

	private static final int TEXT_SIZE = 15;

	private float mDensity;
	private int mValue = 50, mMaxValue = 500, mModType = MOD_TYPE_ONE,
			mLineDivider = ITEM_ONE_DIVIDER;

	private int mLastY, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext())
				.getScaledMinimumFlingVelocity();

		// setBackgroundResource(R.drawable.bg_wheel);
		setBackgroundDrawable(createBackground());
	}

	private GradientDrawable createBackground() {
		float strokeWidth = mDensity; // 边框宽度
		float roundRadius = mDensity; // 圆角半径
		int strokeColor = Color.parseColor("#FFFFFFFF");// 边框颜色

		setPadding((int) strokeWidth, (int) strokeWidth, (int) strokeWidth, 0);

		int colors[] = { 0x00F5F7F7, 0x00F5F7F7, 0x00F5F7F7 };// 分别为开始颜色，中间夜色，结束颜色
		GradientDrawable bgDrawable = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, colors);// 创建drawable
		bgDrawable.setCornerRadius(roundRadius);
		bgDrawable.setStroke((int) strokeWidth, strokeColor);
		return bgDrawable;
	}

	/**
	 * 
	 * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
	 * 
	 * @param value
	 *            初始值
	 * @param maxValue
	 *            最大值
	 * @param model
	 *            刻度盘精度：<br>
	 *            {@link MOD_TYPE_HALF}<br>
	 *            {@link MOD_TYPE_ONE}<br>
	 */
	public void initViewParam(int defaultValue, int maxValue, int model) {
		switch (model) {
		case MOD_TYPE_ONE:
			mModType = MOD_TYPE_ONE;
			mLineDivider = ITEM_ONE_DIVIDER;
			mValue = defaultValue;
			mMaxValue = maxValue;
			break;

		default:
			break;
		}
		invalidate();

		mLastY = 0;
		mMove = 0;
		notifyValueChange();
	}

	/**
	 * 设置用于接收结果的监听器
	 * 
	 * @param listener
	 */
	public void setValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 获取当前刻度值
	 * 
	 * @return
	 */
	public float getValue() {
		return mValue;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mWidth = getWidth();
		mHeight = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawScaleLine(canvas);
		drawMiddleLine(canvas);
	}

	/**
	 * 从中间往两边开始画刻度线
	 * 
	 * @param canvas
	 */
	private void drawScaleLine(Canvas canvas) {
		canvas.save();
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.BLACK);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);

		int height = mHeight, drawCount = 0;
		float yPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

		for (int i = 0; drawCount <= 4 * height; i++) {
			int numSize = String.valueOf(mValue + i).length();

			yPosition = (height / 2 - mMove) + i * mLineDivider * mDensity;
			if (yPosition + getPaddingTop() < mHeight) {
				if ((mValue + i) % mModType == 0) {
					canvas.drawLine(getPaddingLeft(), yPosition, mDensity
							* ITEM_MAX_HEIGHT, yPosition, linePaint);

					if (mValue + i <= mMaxValue) {
						switch (mModType) {
						case MOD_TYPE_ONE:
							canvas.drawText(String.valueOf(mValue + i),
									getWidth() - 3 * textWidth, yPosition
											- (textWidth * numSize / 2),
									textPaint);
							break;
						default:
							break;
						}
					}
				} else {
					canvas.drawLine(getPaddingLeft(), yPosition, mDensity
							* ITEM_MIN_HEIGHT, yPosition, linePaint);
				}
			}

			yPosition = (height / 2 - mMove) - i * mLineDivider * mDensity;
			if (yPosition > getPaddingTop()) {
				if ((mValue - i) % mModType == 0) {
					canvas.drawLine(getPaddingLeft(), yPosition, mDensity
							* ITEM_MAX_HEIGHT, yPosition, linePaint);

					if (mValue - i >= 0) {
						switch (mModType) {
						case MOD_TYPE_ONE:
							canvas.drawText(String.valueOf(mValue - i),
									getWidth() - 3 * textWidth, yPosition
											- (textWidth * numSize / 2),
									textPaint);
							break;

						default:
							break;
						}
					}
				} else {
					canvas.drawLine(getPaddingLeft(), yPosition, mDensity
							* ITEM_MIN_HEIGHT, yPosition, linePaint);
				}
			}

			drawCount += 2 * mLineDivider * mDensity;
		}

		canvas.restore();
	}


	/**
	 * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
	 * 
	 * @param canvas
	 */
	private void drawMiddleLine(Canvas canvas) {
		// TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
		int gap = 12, indexWidth = 8, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
		String color = "#66999999";
		canvas.save();

		Paint redPaint = new Paint();
		redPaint.setStrokeWidth(indexWidth);
		redPaint.setColor(Color.rgb(196, 64, 31));
		canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, redPaint);
		canvas.restore();

		Paint ovalPaint = new Paint();
		ovalPaint.setColor(Color.rgb(196, 64, 31));
		ovalPaint.setStrokeWidth(indexTitleWidth);
		canvas.drawLine(0, mHeight / 2, indexTitleWidth, mHeight / 2, ovalPaint);
		canvas.drawLine(mWidth, mHeight / 2, mWidth - indexTitleWidth,
				mHeight / 2, ovalPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int yPosition = (int) event.getY();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			mScroller.forceFinished(true);

			mLastY = yPosition;
			mMove = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			mMove += (mLastY - yPosition);
			changeMoveAndValue();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			countMoveEnd();
			countVelocityTracker(event);
			return false;
			// break;
		default:
			break;
		}

		mLastY = yPosition;
		return true;
	}

	private void countVelocityTracker(MotionEvent event) {
		mVelocityTracker.computeCurrentVelocity(1000);
		float xVelocity = mVelocityTracker.getXVelocity();
		if (Math.abs(xVelocity) > mMinVelocity) {
			mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 0, 0);
		}
	}

	private void changeMoveAndValue() {
		int tValue = (int) (mMove / (mLineDivider * mDensity));
		if (Math.abs(tValue) > 0) {
			mValue += tValue;
			mMove -= tValue * mLineDivider * mDensity;
			if (mValue <= 0 || mValue > mMaxValue) {
				mValue = mValue <= 0 ? 0 : mMaxValue;
				mMove = 0;
				mScroller.forceFinished(true);
			}
			notifyValueChange();
		}
		postInvalidate();
	}

	private void countMoveEnd() {
		int roundMove = Math.round(mMove / (mLineDivider * mDensity));
		mValue = mValue + roundMove;
		mValue = mValue <= 0 ? 0 : mValue;
		mValue = mValue > mMaxValue ? mMaxValue : mValue;

		mLastY = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();
	}

	private void notifyValueChange() {
		if (null != mListener) {
			if (mModType == MOD_TYPE_ONE) {
				mListener.onValueChange(mValue);
			}
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrY() == mScroller.getFinalY()) {
				countMoveEnd();
			} else {
				int yPosition = mScroller.getCurrY();
				mMove += (mLastY - yPosition);
				changeMoveAndValue();
				mLastY = yPosition;
			}

		}
	}

}
