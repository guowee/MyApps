package com.stcloud.driverecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;

public class MovePhotoFilesTask extends Thread {

	private Fragment mFragment;
	private int mOp;
	private ArrayList<String> mFiles;

	public static final int OP_LOCK = 100;
	public static final int OP_UNLOCK = 101;

	private final int MOVE_PHOTO_TASK_COMPLETE = 1000;

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch(inputMessage.what) {
			case MOVE_PHOTO_TASK_COMPLETE:
				if (mFragment instanceof PhotoFragment) {
					GridViewAdapter adapter = ((PhotoFragment)mFragment).getGridViewAdapter();
					for (int i=0; i<mFiles.size(); i++) {
						for (int j=0; j<adapter.getCount(); j++) {
							if (mFiles.get(i).equals(adapter.getItem(j).getName())) {
								if (adapter.getItem(j).getAbsolutePath().contains("lock")) {
									// move to unlock folder
									adapter.remove(adapter.getItem(j));
									adapter.add(new File(FileExplorer.DIR_PHOTO_UNLOCK + mFiles.get(i)));
									adapter.sort(new SortFileName());
								} else {
									// move to lock folder
									adapter.remove(adapter.getItem(j));
									adapter.add(new File(FileExplorer.DIR_PHOTO_LOCK + mFiles.get(i)));
									adapter.sort(new SortFileName());
								}
							}
						}
					}
					adapter.notifyDataSetChanged();
				}
				break;
			}			
		}
	};

	public MovePhotoFilesTask(Fragment fragment, int op, ArrayList<String> files) {
		mFragment = fragment;
		mOp = op;
		mFiles = files;
	}
	
	@Override
	public void run() {
		// move files from source to destination
		if (mOp == OP_LOCK) {
			File lockDir = new File(FileExplorer.DIR_PHOTO_LOCK);
			if (!lockDir.exists())
				lockDir.mkdirs();
			
			for (int i=0; i<mFiles.size(); i++) {
				photoLock(mFiles.get(i));
			}
		} else if (mOp == OP_UNLOCK) {
			for (int i=0; i<mFiles.size(); i++) {
				photoUnlock(mFiles.get(i));
			}
		}

		Message msg = mHandler.obtainMessage(MOVE_PHOTO_TASK_COMPLETE);
		mHandler.sendMessage(msg);
	}

	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private void photoLock(String filename)
	{
		File src = null;
		File dst = null;
		
		src = new File(FileExplorer.DIR_PHOTO_UNLOCK + filename);
		dst = new File(FileExplorer.DIR_PHOTO_LOCK + filename);

		try {
			copy(src, dst);
			src.delete();		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void photoUnlock(String filename)
	{
		File src = null;
		File dst = null;
		
		src = new File(FileExplorer.DIR_PHOTO_LOCK + filename);
		dst = new File(FileExplorer.DIR_PHOTO_UNLOCK + filename);

		try {
			copy(src, dst);
			src.delete();		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}		 

}
