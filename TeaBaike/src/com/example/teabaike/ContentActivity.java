package com.example.teabaike;

import java.util.Map;


import com.example.config.MyConfig;
import com.example.helper.HttpClientHelper;
import com.example.helper.JSONHelper;
import com.example.helper.SQLiteDatabaseHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentActivity extends Activity {

	/** 这个网页的标题 */
	private TextView textView_content_title;
	/** 创建时间信息 */
	private TextView textView_content_create_time;
	/** 资源 */
	private TextView textView_content_source;
	/** 显示网页的控件 */
	private WebView webView_content_wap_content;
	/** 表示已经查看了，但还未收藏 */
	public static final String NOT_COLLECT = "1";
	/** 表示已经查看了，并且收藏了 */
	public static final String COLLECT = "2";

	/** 当前这个页面请求网络得到的值 */
	private Map<String, Object> mapValue;

	SQLiteDatabaseHelper dataBase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_content);

		initialLayout();

		initialData();

	}

	/**
	 * 初始化布局
	 */
	private void initialLayout() {

		textView_content_title = (TextView) findViewById(R.id.textView_content_title);

		textView_content_create_time = (TextView) findViewById(R.id.textView_content_create_time);

		textView_content_source = (TextView) findViewById(R.id.textView_content_source);

		webView_content_wap_content = (WebView) findViewById(R.id.webView_content_wap_content);
	}

	/**
	 * 初始化数据
	 */
	private void initialData() {

		dataBase = new SQLiteDatabaseHelper(ContentActivity.this);

		String id = getIntent().getStringExtra("id");

		if (id != null) {
			new ContentTask(ContentActivity.this).execute(MyConfig.CONTENT
					+ "&id=" + id);
		}

	}

	class ContentTask extends AsyncTask<String, Void, Object> {

		/** 上下文对象 */
		private Context context;
		/** 加载数据的提示对话框 */
		private ProgressDialog pDialog;

		public ContentTask(Context context) {

			this.context = context;

			pDialog = new ProgressDialog(this.context);

			pDialog.setTitle("请稍后");

			pDialog.setMessage("正在加载请稍后...");

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}

		@Override
		protected Object doInBackground(String... params) {

			String jsonString = HttpClientHelper.loadTextFromURL(params[0],
					"UTF-8");

			Map<String, Object> map = JSONHelper.jsonStringToMap(jsonString,
					new String[] { "id", "title", "source", "wap_content",
							"create_time" }, "data");
			if (map != null && map.size() != 0) {

				/** 添加到数据库 */
				String id = map.get("id").toString();

				String title = map.get("title").toString();

				String source = map.get("source").toString();

				String create_time = map.get("create_time").toString();

				String sql = "INSERT INTO tb_teacontents(_id,title,source,create_time,type) values (?,?,?,?,?)";

				boolean isRefresh = dataBase.updataData(sql, new Object[] { id,
						title, source, create_time, NOT_COLLECT });// 表示已经查看过

			}

			return map;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			if (result != null) {

				mapValue = (Map<String, Object>) result;

				textView_content_title
						.setText(mapValue.get("title").toString());
				textView_content_source.setText(mapValue.get("source")
						.toString());
				textView_content_create_time.setText(mapValue
						.get("create_time").toString());
				webView_content_wap_content.loadDataWithBaseURL(null, mapValue
						.get("wap_content").toString(), "text/html", "UTF-8",
						null);

			}
			pDialog.dismiss();

		}

	}

	public void clickButton(View view) {

		switch (view.getId()) {

		case R.id.imageView_content_back:
			// 返回按钮
			this.finish();

			break;

		case R.id.imageView_content_collect:
			// 收藏按钮
			String sql = "UPDATE tb_teacontents SET type = ? WHERE _id = ?";

			if (mapValue != null && !mapValue.equals("")) {

				String id = mapValue.get("id").toString();

				dataBase.updataData(sql, new Object[] { COLLECT, id });// 收藏

				Toast.makeText(ContentActivity.this, "收藏成功", Toast.LENGTH_LONG)
						.show();
			}

			break;

		case R.id.imageView_content_share:
			// 分享按钮
			Intent intent = new Intent(ContentActivity.this,
					ShareActivity.class);
			startActivity(intent);
			
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);// 下往上推出效果
			break;

		default:
			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭数据库
		if (dataBase != null) {
			dataBase.destroy();
		}
	}

}
