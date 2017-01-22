package com.stcloud.driverecorder;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<File> {

	private Context mContext;
	private int mLayoutResID;
	private ArrayList<File> mItems;
	private static boolean mBusy = false;

	public GridViewAdapter(Context context, int layoutResourceID, ArrayList<File> objects) {
		super(context, layoutResourceID, objects);
		mContext = context;
		mLayoutResID = layoutResourceID;
		mItems = objects;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
			view = inflater.inflate(mLayoutResID, parent, false);
		} else {
			view = convertView;
		}

		File file = mItems.get(position);
		
		TextView textView = (TextView) view.findViewById(R.id.grid_text);
		textView.setText(file.getName());
		textView.setLines(2);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.grid_image);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	    imageView.setImageDrawable(Utils.getIcon(mContext, file));
	    imageView.setTag(file);
        if (!mBusy) {
	        String mime = Utils.getMime(file);
	        if (mime != null && (mime.contains("image") || mime.contains("video"))) {
				new CreateThumbnailTask(imageView).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, 
						file.getAbsolutePath(), file.getName(), "140", "140", "grid");
	        }
        }
	    
        ImageView checkImg = (ImageView)view.findViewById(R.id.lockImg);
        if (file.getAbsolutePath().contains("lock")) {
        	checkImg.setVisibility(View.VISIBLE);
        } else {
        	checkImg.setVisibility(View.INVISIBLE);
        }
        
		return view;
	}

	public final int getCount() {
		return mItems.size();
	}

	public final File getItem(int position) {
		return mItems.get(position);
	}

	public final long getItemId(int position) {
		return position;
	}

	public void setBusyState(boolean state) {
		mBusy = state;
	}
}
