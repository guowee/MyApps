package com.hipad.smarthome.adapter;

import java.util.ArrayList;
import com.hipad.smarthome.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author EthanChung
 */
public class SceneGridViewAdapter extends ArrayAdapter<Scene> {
	String TAG = "SceneGridViewAdapter";
	Context context;
	int layoutResourceId;
	ArrayList<Scene> data = new ArrayList<Scene>();
	int size;

	public SceneGridViewAdapter(Context context, int layoutResourceId, ArrayList<Scene> data, int imgSize) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.size = imgSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		Scene s = data.get(position);
		//int titleResId = getResourceId("scene_"+Scene.getIdBySceneCmd(s.getSceneCmd()), "string", context.getPackageName());
		//int imateResId = getResourceId("scene_"+Scene.getIdBySceneCmd(s.getSceneCmd()) + "_" + context.getString(size), "drawable", context.getPackageName());
		int titleResId = Scene.getSceneTitleResId(context, s);
		int imateResId = Scene.getSceneImgResId(context, s, size);
		holder.txtTitle.setText(context.getString(titleResId));
		holder.imageItem.setImageResource(imateResId);
		return row;
	}

//	public int getResourceId(String pVariableName, String pResourcename,
//			String pPackageName) {
//		try {
//			return context.getResources().getIdentifier(pVariableName, pResourcename,
//					pPackageName);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return -1;
//		}
//	}
}

class RecordHolder {
	TextView txtTitle;
	ImageView imageItem;
}