package com.example.teabaike;

import java.util.List;
import java.util.Map;

import com.example.app.MyApplication;
import com.example.config.MyConfig;
import com.example.fragment.CollectFragment;
import com.example.fragment.ContentFragment;
import com.example.fragment.CopyrightFragment;
import com.example.fragment.FeedbackFragment;
import com.example.fragment.FunctionTeaFragment;
import com.example.fragment.FunctionTeaFragment.MyButtonClickListener;
import com.example.fragment.MapFragment;
import com.example.fragment.QRCodeFragment;
import com.example.helper.HttpClientHelper;
import com.example.helper.JSONHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchActivity extends FragmentActivity implements
		MyButtonClickListener {

	/** 可以放fragment的控件 */
	private FrameLayout frameLayout_fun;
	/** 返回上一页 */
	private ImageView imageView_fun_return;
	/** 标题栏显示的文字 */
	private TextView textView_fun_title;
	/** Fragment管理器 */
	private FragmentManager fragmentManager;
	/** 每一个fragment */
	private Fragment fragment;
	/** 缓存图片的map */
	private Map<String, Bitmap> cacheImageMap;

	/** 界面中显示的标记文字,0:查百科，1：我的收藏，2浏览记录，3：版权信息，4：意见反馈，5：二维码，6：查看地图，7：搜索内容 */
	private String[] titleName = { "查百科", "我的收藏", "浏览记录", "版权信息", "意见反馈",
			"二维码", "查看地图", "" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_search);

		int titleTag = initialData();

		frameLayout_fun = (FrameLayout) findViewById(R.id.frameLayout_fun);

		imageView_fun_return = (ImageView) findViewById(R.id.imageView_fun_return);

		textView_fun_title = (TextView) findViewById(R.id.textView_fun_title);

		imageView_fun_return.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onKeyDown(4, null);

			}
		});

		initialFragment(titleTag, null);

	}

	/*
	 * 当按返回键的时候fragment开始出栈 (non-Javadoc)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {

			textView_fun_title.setText(titleName[0]);

			if (fragmentManager.getBackStackEntryCount() == 1) {
				finish();
			} else {
				fragmentManager.popBackStack();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private int initialData() {

		cacheImageMap = ((MyApplication) this.getApplication())
				.getCacheImageMap();

		fragmentManager = this.getSupportFragmentManager();

		String titleTagStr = getIntent().getStringExtra("titleTagStr");

		int titleTag = 0;

		titleTag = Integer.parseInt(titleTagStr);

		return titleTag;

	}

	private void initialFragment(int titleTag, String text) {

		String titleStr = "";

		switch (titleTag) {

		case 0:

			titleStr = titleName[0];

			fragment = new FunctionTeaFragment(SearchActivity.this);

			break;

		case 1:

			titleStr = titleName[1];

			fragment = new CollectFragment("2", SearchActivity.this);

			break;
		case 2:

			titleStr = titleName[2];

			fragment = new CollectFragment("1", SearchActivity.this);

			break;
		case 3:

			titleStr = titleName[3];

			fragment = new CopyrightFragment();

			break;
		case 4:

			titleStr = titleName[4];

			fragment = new FeedbackFragment();

			break;

		case 5:

			titleStr = titleName[5];

			fragment = new QRCodeFragment();

			break;
		case 6:

			titleStr = titleName[6];

			fragment = new MapFragment();

			break;

		case 7:

			textView_fun_title.setText(text);

			String urlStr = MyConfig.SEARCH + "&search=" + text;

			new SearchTask(SearchActivity.this, urlStr).execute(urlStr);

			return;

		default:
			break;
		}

		if (fragment != null) {

			fragment.setArguments(getIntent().getExtras());

			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			fragmentTransaction.addToBackStack(titleStr);
			// 设置每个fragment显示标题
			textView_fun_title.setText(titleStr);

			fragmentTransaction.replace(R.id.frameLayout_fun, fragment)
					.commit();
		}

	}

	class SearchTask extends AsyncTask<String, Void, Object> {

		private Context context;

		private ProgressDialog pDialog;

		private String urlStr;

		public SearchTask(Context context, String urlStr) {

			this.context = context;

			this.urlStr = urlStr;

			pDialog = new ProgressDialog(this.context);

			pDialog.setTitle("请稍等");

			pDialog.setMessage("正在加载...");

		}

		@Override
		protected Object doInBackground(String... params) {

			String jsonString = HttpClientHelper.loadTextFromURL(params[0],
					"UTF-8");// 网络访问得到数据

			List<Map<String, Object>> list = JSONHelper.jsonStringToList(
					jsonString, new String[] { "title", "source", "nickname",
							"create_time", "wap_thumb", "id" }, "data");
			return list;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog.show();
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			fragment = new ContentFragment(urlStr,
					(List<Map<String, Object>>) result, cacheImageMap);

			fragment.setArguments(getIntent().getExtras());

			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			fragmentTransaction.addToBackStack(null);

			fragmentTransaction.replace(R.id.frameLayout_fun, fragment)
					.commit();

			pDialog.dismiss();
		}

	}

	@Override
	public void onMyButtonClick(int titleTag, String text) {

		initialFragment(titleTag, text);
	}

}
