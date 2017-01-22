package com.stcloud.driverecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ListPhotoFilesTask extends Thread {

	private final int LIST_TASK_COMPLETE = 1000;
	
	private Fragment mFragment;
	private String mPath;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			ArrayList<File> files = (ArrayList<File>)inputMessage.obj;
			
			switch(inputMessage.what) {
			case LIST_TASK_COMPLETE:
				if (mFragment.getActivity() != null) { // this check is merely a workaround
					mFragment.getActivity().findViewById(R.id.photo_progress_large).setVisibility(View.INVISIBLE);
					GridViewAdapter gridAdapter = new GridViewAdapter(mFragment.getActivity(), R.layout.grid_item, files);
					if (mFragment instanceof PhotoFragment) {
						GridView gridView = ((PhotoFragment)mFragment).getGridView();
						gridView.setAdapter(gridAdapter);
						gridView.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								((PhotoFragment)mFragment).onGridItemClick((GridView) parent, view, position, id);
							}
						});
					}
				}
				break;
			}
		}
	};
	
	public ListPhotoFilesTask(Fragment fragment, String path) {
		mFragment = fragment;
		mPath = path;
	}
	
	/**
	 * Collect photos from unlocked directory and locked directory
	 */
	public void run() {
		ArrayList<File> items = new ArrayList<File>();

		// collect from unlocked directory
		// ...
		
		//File f = new File(mPath);
		File unlockDir = new File(FileExplorer.DIR_PHOTO_UNLOCK);
		File[] unlockFiles = unlockDir.listFiles();

		if (unlockFiles != null) {
			List<File> directoryListing = new ArrayList<File>();
			for (int i=0; i<unlockFiles.length; i++) {
				if (unlockFiles[i].isFile()) {
					String mime = Utils.getMime(unlockFiles[i]);
					if (mime != null && mime.contains("image"))
						directoryListing.add(unlockFiles[i]);
				}
			}

			for(int i=0; i < directoryListing.size(); i++)
			{
				File file = directoryListing.get(i);
				items.add(file);
			}
		}
		
		// collect from locked directory
		// ...
		File lockDir = new File(FileExplorer.DIR_PHOTO_LOCK);
		File[] lockFiles = lockDir.listFiles();

		if (lockFiles != null) {
			List<File> directoryListing = new ArrayList<File>();
			// only collect image files
			for (int i=0; i<lockFiles.length; i++) {
				if (lockFiles[i].isFile() && Utils.getMime(lockFiles[i]).contains("image")) {
					directoryListing.add(lockFiles[i]);
				}
			}

			for(int i=0; i < directoryListing.size(); i++)
			{
				File file = directoryListing.get(i);
				items.add(file);
			}
		}
		
		Collections.sort(items, new SortFileName());

		Message msg = mHandler.obtainMessage(LIST_TASK_COMPLETE, items);
		msg.sendToTarget();
	}
}
