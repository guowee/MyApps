package com.haomee.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.haomee.util.ViewUtil;

/**
 * 宽度为屏幕宽度 高度根据图片宽高比自适应
 * 
 * @author xwh817
 * 
 */
public class AutoHeightImageView extends ImageView {

	public AutoHeightImageView(Context context) {
		super(context);
	}

	public AutoHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		Bitmap bitmap = null;
		if (bd != null) {
			bitmap = bd.getBitmap();
		}
		if (bitmap != null) {
			// this.setScaleType(ScaleType.FIT_XY);
			// 在eclipse设计器从预览会报错，其实实际运行是好的，fragment的问题
			Context context = this.getContext();
			if (context instanceof Activity) {

				int screen_width = ViewUtil.getScreenWidth((Activity) context);

				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				if (width > 0) {
					ViewGroup.LayoutParams params = this.getLayoutParams();
					params.width = screen_width;
					params.height = screen_width * height / width;
					this.setLayoutParams(params);
				}
			}

		}

	}

}
