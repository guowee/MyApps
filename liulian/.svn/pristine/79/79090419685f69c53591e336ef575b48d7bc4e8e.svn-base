package com.haomee.util;

import java.io.InputStream;
import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.View.MeasureSpec;

public class ImageUtil {

	/**
	 * 根据文件名获取图片src
	 */
	public static int getResIdByName(Context context, String fileName) {

		int id = -1;
		try {
			// 使用Java反射机制获取R中的静态变量。
			Class<?> raw = Class.forName(context.getPackageName() + ".R$drawable"); // 注意内部类的写法

			Field simp = raw.getField(fileName); // 反射获取属性
			id = (Integer) simp.get(null); // 静态变量与具体对象无关，传null		

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap getLocalImage(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	// 在不加载图片的情况下，获取图片高度
	public static int[] getImageSize(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		/** 
		 * 最关键在此，把options.inJustDecodeBounds = true; 
		 * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了 
		 */
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null  
		/** 
		 *options.outHeight为原始图片的高 
		 */
		int[] size = new int[2];
		size[0] = options.outWidth;
		size[1] = options.outHeight;
		return size;
	}

	// 将图片的四角圆化
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		//得到画布
		Canvas canvas = new Canvas(output);

		//将画布的四角圆化
		final int color = Color.RED;
		final Paint paint = new Paint();
		//得到与图像相同大小的区域  由构造的四个值决定区域的位置以及大小
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		//drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	
	// 获取界面图片
	public static Bitmap loadBitmapFromView(View view) {
		Bitmap b = null;
		try{
			b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			view.draw(c);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return b;
		
		/*view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        
        return bitmap;*/
	}

}
