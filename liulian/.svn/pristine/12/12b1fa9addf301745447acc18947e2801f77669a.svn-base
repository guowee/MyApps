package com.haomee.liulian;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.haomee.adapter.TopicTypeAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.entity.TopicType;
import com.haomee.util.ImageUtil;

/**
 * 话题类别
 */
public class TopicTypeActivity extends BaseActivity {

	private ImageView img_bg;
	private GridView grid_types;
	private int topic_type;
	
	private static int RESUlT_CODE = 1;
	//private View layout_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_topic_type);

		//layout_bg = this.findViewById(R.id.layout_bg);

		img_bg = (ImageView) this.findViewById(R.id.img_bg);
		grid_types = (GridView) this.findViewById(R.id.grid_types);

		TopicTypeAdapter adapter = new TopicTypeAdapter(this);
		ArrayList<TopicType> topicTypes = new ArrayList<TopicType>();
		for (int i = 0; i <= 13; i++) {
			TopicType type = new TopicType();
			type.setId(i);
			type.setName(CommonConst.topic_types[i]);
			type.setIcon_res(ImageUtil.getResIdByName(this, "home_label_" + i));

			topicTypes.add(type);
		}

		adapter.setData(topicTypes);

		grid_types.setAdapter(adapter);

		findViewById(R.id.bt_close).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		grid_types.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				topic_type = position;
				Intent intent = new Intent();
				intent.putExtra("type", topic_type);
				setResult(RESUlT_CODE, intent);
				TopicTypeActivity.this.finish();
				StatService.onEvent(TopicTypeActivity.this, "count_of_category", "各个分类点击次数", 1);
			}
		});

		
		/*if (DiscoverFragment.view_bitmap != null) {
			Log.i("test", "DiscoverFragment.view_bitmap:"+DiscoverFragment.view_bitmap);
			try{
				img_bg.setImageBitmap(DiscoverFragment.view_bitmap);
				img_bg.setAlpha(0.2f);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			// 毛玻璃背景
			setBg();
		}*/
		
				
	}

	/*@SuppressLint("NewApi")
	private void setBg() {
		new AsyncTask<Object, Object, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Object... params) {
				Bitmap bg = null;
				if (DiscoverFragment.view_bitmap != null) {
					bg = BitmapUtil.fastblur(TopicTypeActivity.this, DiscoverFragment.view_bitmap, 24, true);
				}

				return bg;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					img_bg.setImageBitmap(result);
					img_bg.setAlpha(0.5f);
				}
			}

		}.execute();
	}
	*/

}
