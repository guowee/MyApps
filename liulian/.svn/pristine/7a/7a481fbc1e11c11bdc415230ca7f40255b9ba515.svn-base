package com.haomee.adapter;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.haomee.entity.Image;
import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;

public class ImageChoiceAdapter extends BaseAdapter {

	private List<Image> list_image;
	private Activity context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;

	public ImageChoiceAdapter(Activity context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Image> list_image) {
		this.list_image = list_image;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_image.size() >= 20 ? 20 : list_image.size();
	}

	@Override
	public Object getItem(int position) {
		return list_image.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_choice_images, null);
			viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
			convertView.setTag(viewHolder);
			ViewGroup.LayoutParams params = viewHolder.item_image.getLayoutParams();
			params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 15)) / 3;
			params.height = params.width;
			viewHolder.item_image.setLayoutParams(params);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (list_image.get(position).getMime_type() == null && list_image.get(position).getFilePath() == null && list_image.get(position).getSize() == null) {
			InputStream input = context.getResources().openRawResource(Integer.parseInt(list_image.get(position).getId()));
			@SuppressWarnings("deprecation")
			BitmapDrawable pic = new BitmapDrawable(input);
			Bitmap bitmap = pic.getBitmap();
			viewHolder.item_image.setImageBitmap(bitmap);
			//			  viewHolder.rl_bg.setBackgroundResource(resid);
		} else {
			/*BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;   
  	        opt.inPurgeable = true;  
  	        opt.inInputShareable = true;       	       
  	        opt.inJustDecodeBounds = false;
 	        opt.inSampleSize = 8;   // 缩略倍数
*/			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), Long.parseLong(list_image.get(position).getId()), Thumbnails.MICRO_KIND, null);
			viewHolder.item_image.setImageBitmap(bitmap);

		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView item_image;
	}

}
