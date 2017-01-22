package com.hipad.smarthome.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
/**
 * 
 * @author guowei
 *
 */
public class CanvasUtils {

	public static Bitmap createProgressBitmap(Bitmap bitmap, float progress,
			float ratio) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xffd1d1d1;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		float startAngle = 135 + ratio * progress;
		float totalAngle = 360 - ratio * progress;
		canvas.drawArc(rectF, startAngle, totalAngle, true, paint);

		return output;
	}

	public static void getProgressBitmap(ImageView view, Resources resource,
			int imgId, float progress, float ratio) {

		/*
		 * if (progress > 100 || progress < 0) { return; }
		 */

		BitmapDrawable bmpDraw = (BitmapDrawable) resource.getDrawable(imgId);

		Bitmap bitmap = bmpDraw.getBitmap();

		Bitmap result = createProgressBitmap(bitmap, progress, ratio);

		view.setImageBitmap(result);
	}

}
