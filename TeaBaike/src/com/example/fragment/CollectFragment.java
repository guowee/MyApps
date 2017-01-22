package com.example.fragment;

import java.util.List;
import java.util.Map;

import com.example.helper.SQLiteDatabaseHelper;
import com.example.teabaike.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CollectFragment extends BaseListFragment {

	/** 数据库操作 */
	private SQLiteDatabaseHelper database;
	/** listView的数据源 */
	private List<Map<String, String>> list = null;
	/** listView的adapter */
	private CollectAdapter collectAdapter;
	/** 不要首页广告 */
	private RelativeLayout relative_fragment_content;

	/** 表示已经查看了，但还未收藏 */
	public static final String NOT_COLLECT = "1";
	/** 表示已经查看了，并且收藏了 */
	public static final String COLLECT = "2";

	public CollectFragment() {

	}

	public CollectFragment(String type, Context context) {

		database = new SQLiteDatabaseHelper(context);

		if (NOT_COLLECT.equals(type)) {

			String sql = "SELECT * FROM tb_teacontents";

			list = database.SelectData(sql, null);

		} else if (COLLECT.equals(type)) {

			String sql = "SELECT * FROM tb_teacontents WHERE type = ?";

			list = database.SelectData(sql, new String[] { "2" });

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		relative_fragment_content = (RelativeLayout) view
				.findViewById(R.id.relative_fragment_content);
		relative_fragment_content.setVisibility(View.GONE);
		listview.setPullLoadEnable(false);// 不加载了

		if (list != null && list.size() != 0) {

			collectAdapter = new CollectAdapter(getActivity(), list);

			listview.setAdapter(collectAdapter);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String idStr = list.get((int) id).get("_id").toString();

					SkipContentActivity(idStr);

				}

			});
		}

		return view;

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}

	class CollectAdapter extends BaseAdapter {

		private Context context;
		private List<Map<String, String>> list;

		public CollectAdapter(Context context, List<Map<String, String>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder mhHolder;

			if (convertView == null) {

				mhHolder = new ViewHolder();

				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_listview, null);

				mhHolder.title = (TextView) convertView
						.findViewById(R.id.title);

				mhHolder.source = (TextView) convertView
						.findViewById(R.id.source);

				mhHolder.create_time = (TextView) convertView
						.findViewById(R.id.create_time);

				convertView.setTag(mhHolder);

			} else {

				mhHolder = (ViewHolder) convertView.getTag();
			}

			String title = (String) list.get(position).get("title");

			String source = (String) list.get(position).get("source");

			String create_time = (String) list.get(position).get("create_time");

			mhHolder.title.setText(title);

			mhHolder.source.setText(source);

			mhHolder.create_time.setText(create_time);

			return convertView;
		}

		class ViewHolder {

			private TextView title;

			private TextView source;

			private TextView create_time;

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭数据库
		if (database != null) {

			database.destroy();
		}
	}

}
