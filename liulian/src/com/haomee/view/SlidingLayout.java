package com.haomee.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * 自定义展开和收起
 *
 */
public class SlidingLayout extends FrameLayout {

	private static final int step_length_default = 20;
	private static final int step_during_default = 20;

	private View child;
	//private int child_height;

	public boolean is_opening;
	private int step_length; // 每次移动距离
	private int step_during;
	private int offset_height;

	public SlidingLayout(Context context) {
		super(context);
	}

	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param length	每步距离
	 * @param during	每步间隔
	 */
	public void inital(int length, int during, boolean status_open) {

		if (this.getChildCount() != 0) {
			child = this.getChildAt(0);
		} else {
			Log.e("SlidingLayout", "参数个数不对");
		}

		step_length = length;
		step_during = during;
		is_opening = status_open;

		// 默认初始关掉
		if (!status_open) {

			ViewTreeObserver vto = this.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				boolean is_vto = false;

				@Override
				public void onGlobalLayout() {
					if (!is_vto) {
						offset_height = child.getHeight();

						if (offset_height > 0) {
							is_vto = true;

							FrameLayout.LayoutParams layoutParams_move = (FrameLayout.LayoutParams) child.getLayoutParams();
							layoutParams_move.topMargin = -offset_height;
							child.setLayoutParams(layoutParams_move);

							child.setVisibility(VISIBLE);
						}
					}

				}
			});

		}

	}
	
	public void toggle(){
		if(is_opening){
			close();
		}else{
			open();
		}
	}
	

	public void open() {

		if (step_length == 0) {
			step_length = step_length_default;
		}
		if (step_during == 0) {
			step_during = step_during_default;
		}

		this.is_opening = true;
		handler_move_window.removeMessages(0);
		handler_move_window.sendEmptyMessage(0);
	}

	public void close() {
		this.is_opening = false;
		handler_move_window.removeMessages(0);
		handler_move_window.sendEmptyMessage(0);
	}

	Handler handler_move_window = new Handler() {
		public void handleMessage(Message msg) {
			if (is_opening) {
				offset_height -= step_length;
				if (offset_height > 0) {
					this.sendEmptyMessageDelayed(0, step_during); // 自己不停循环
				} else {
					offset_height = 0;
				}

			} else {
				offset_height += step_length;
				if (offset_height < child.getHeight()) {
					this.sendEmptyMessageDelayed(0, step_during); // 自己不停循环
				} else {
					offset_height = child.getHeight();
				}

			}

			try {
				FrameLayout.LayoutParams layoutParams_move = (FrameLayout.LayoutParams) child.getLayoutParams();
				layoutParams_move.topMargin = -offset_height;
				child.setLayoutParams(layoutParams_move);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

}
