package com.stcloud.driverecorder;

import java.io.File;
import java.util.concurrent.Executors;

import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;

public class GridScrollListener implements OnScrollListener {

	public void onScroll(AbsListView view, int firstVisibleItem,
	        int visibleItemCount, int totalItemCount) {
	}
	
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		GridView gview = (GridView)view;
		
	    switch (scrollState) {
	    case OnScrollListener.SCROLL_STATE_IDLE:
    		((GridViewAdapter)gview.getAdapter()).setBusyState(false);
	        int count = view.getChildCount();
	        for (int i = 0; i < count; i++) {
	    		ImageView imageView = (ImageView)view.getChildAt(i).findViewById(R.id.grid_image);
	    		File file = (File)imageView.getTag();
		        String mime = Utils.getMime(file);
		        if (mime != null && (mime.contains("image") || mime.contains("video"))) {
		        	new CreateThumbnailTask(imageView).executeOnExecutor(Executors.newCachedThreadPool(), 
			        		file.getAbsolutePath(), file.getName(), "140", "140", "grid");
		        }
	        }
	        break;
	    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
    		((GridViewAdapter)gview.getAdapter()).setBusyState(true);
	        break;
	    case OnScrollListener.SCROLL_STATE_FLING:
    		((GridViewAdapter)gview.getAdapter()).setBusyState(true);
	        break;
	    }
	}
}
