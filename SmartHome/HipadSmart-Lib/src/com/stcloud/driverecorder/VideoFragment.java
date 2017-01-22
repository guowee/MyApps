package com.stcloud.driverecorder;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.GridView;

public class VideoFragment extends Fragment {

	private GridView mGrid;
	private String mPath;

	private ActionMode mGridActionMode;

	
	
	public static VideoFragment newInstance() {
		VideoFragment f = new VideoFragment();

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPath = getArguments() != null ? getArguments().getString("path") : "/";

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.video_fragment, container, false);
		mGrid = (GridView) rootView.findViewById(R.id.video_grid);
		mGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		mGrid.setMultiChoiceModeListener(new GridMultiChoiceModeListener(getActivity(), getActivity().getMenuInflater()));
		mGrid.setOnScrollListener(new GridScrollListener());

		getActivity().invalidateOptionsMenu();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			new ListVideoFilesTask(this, mPath).start();
			getActivity().findViewById(R.id.video_progress_large).setVisibility(View.VISIBLE);
		}
	}	

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onGridItemClick(GridView g, View v, int position, long id) {
		GridViewAdapter adapter = (GridViewAdapter)mGrid.getAdapter();
		boolean isDir = adapter.getItem(position).isDirectory();
		
		if (isDir) {

		} else {
			File file = adapter.getItem(position);
	    	URI uri = file.toURI();
		    MimeTypeMap map = MimeTypeMap.getSingleton();
		    String ext = null;
		    try {
		    	ext = MimeTypeMap.getFileExtensionFromUrl(URLEncoder.encode(uri.toString(), "UTF-8"));
		    } catch (Exception ex) {
		    	ex.printStackTrace();
		    }
		    String type = map.getMimeTypeFromExtension(ext.toLowerCase());

		    if (type != null) {
			    Intent intent = new Intent(Intent.ACTION_VIEW);
			    Uri data = Uri.fromFile(file);
			    intent.setDataAndType(data, type);
			    startActivity(intent);
		    } else {

		    }
		}
	}

	public GridView getGridView() {
		return mGrid;
	}

	public GridViewAdapter getGridViewAdapter() {
		return (GridViewAdapter)mGrid.getAdapter();
	}

	public String getCurrentPath() {
		return mPath;
	}

	public void finishActionMode() {
		if (mGridActionMode != null) {
			mGridActionMode.finish();
			mGridActionMode = null;
		}
	}

	public class GridMultiChoiceModeListener implements GridView.MultiChoiceModeListener {
		
		private MenuInflater mMenuInflater;
		private Context mContext;
		private ArrayList<File> mSelectedFiles;
		
		public GridMultiChoiceModeListener(Context context, MenuInflater mi) {
			mContext = context;
			mMenuInflater = mi;
			mSelectedFiles = new ArrayList<File>();
		}
		
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        	mGridActionMode = mode;
            mMenuInflater.inflate(R.menu.cab_menu, menu);
            mode.setTitle("Select Items");
            setSubtitle(mode);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch (item.getItemId()) {
        	case R.id.lock:
        	{
        		ArrayList<String> paths = new ArrayList<String>();
        		for (int i=0; i<mSelectedFiles.size(); i++) {
        			// filter already locked file
        			if (!mSelectedFiles.get(i).getAbsolutePath().contains("lock"))
        				paths.add(mSelectedFiles.get(i).getName());
        		}
        		
        		if (paths.size() > 0)
        			new MoveVideoFilesTask(VideoFragment.this, MoveVideoFilesTask.OP_LOCK, paths).start();

				mode.finish();
				break;
        	}
        	case R.id.unlock:
        	{
        		ArrayList<String> paths = new ArrayList<String>();
        		for (int i=0; i<mSelectedFiles.size(); i++) {
        			// filter already unlocked file
        			if (mSelectedFiles.get(i).getAbsolutePath().contains("lock"))
        				paths.add(mSelectedFiles.get(i).getName());
        		}
        		
        		if (paths.size() > 0)
        			new MoveVideoFilesTask(VideoFragment.this, MoveVideoFilesTask.OP_UNLOCK, paths).start();

				mode.finish();
        		break;
        	}
        	}
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        	mSelectedFiles.clear();
        	mGridActionMode = null;
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            GridViewAdapter adapter = (GridViewAdapter)mGrid.getAdapter();
            if (checked == true) { 
            	if (false == mSelectedFiles.contains(adapter.getItem(position)))
            		mSelectedFiles.add(adapter.getItem(position));
            } else { 
            	mSelectedFiles.remove(adapter.getItem(position));
            }
            setSubtitle(mode);
        }

        private void setSubtitle(ActionMode mode) {
            final int checkedCount = mGrid.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(mContext.getString(R.string.one_item_selected));
                    break;
                default:
                    mode.setSubtitle(mContext.getString(R.string.n_items_selected, checkedCount));
                    break;
            }
        }
    }
	
	
}
