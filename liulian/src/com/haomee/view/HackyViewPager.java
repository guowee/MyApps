package com.haomee.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 通过setPageTransformer，自定义ViewPager的切换效果
 *
 */
@SuppressLint("NewApi")
public class HackyViewPager extends ViewPager {
	public HackyViewPager(Context paramContext) {
		super(paramContext);
		initialize();
	}

	public HackyViewPager(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initialize();
	}

	private void initialize() {
		setPageTransformer(true, new ViewPager.PageTransformer() {
			public void transformPage(View paramAnonymousView, float paramAnonymousFloat) {
				if ((paramAnonymousFloat < 0.0F) || (paramAnonymousFloat >= 1.0F)) {
					paramAnonymousView.setTranslationX(0.0F);
					paramAnonymousView.setAlpha(1.0F);
					paramAnonymousView.setScaleX(1.0F);
					paramAnonymousView.setScaleY(1.0F);
					return;
				}
				paramAnonymousView.setTranslationX(-paramAnonymousFloat * paramAnonymousView.getWidth());
				paramAnonymousView.setAlpha(Math.max(0.0F, 1.0F - paramAnonymousFloat));
				float f = Math.max(0.0F, 1.0F - 0.3F * paramAnonymousFloat);
				paramAnonymousView.setScaleX(f);
				paramAnonymousView.setScaleY(f);
			}
		});
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		try {
			return super.onInterceptTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}