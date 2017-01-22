package com.haomee.liulian;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.view.CircleImageView;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SendMovieContent extends BaseActivity {

	private Context activity_context;
	private String topic_id;
	private TextView content_title;

	private ImageView tv_image;
	private TextView publish;
	private EditText et_text;
	private CircleImageView user_icon;
	private TextView user_name;
	private String movie_id;
	public static final int SERACHMUSIC = 4;
	private String head_pic;
	private String title;
	
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_content_movie);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		loadingDialog = new LoadingDialog(this);

		activity_context = this;

		tv_image = (ImageView) findViewById(R.id.tv_image);
		publish = (TextView) findViewById(R.id.publish);
		et_text = (EditText) findViewById(R.id.et_text);
		et_text.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		user_icon = (CircleImageView) findViewById(R.id.user_icon);
		user_name = (TextView) findViewById(R.id.name);
		content_title = (TextView) findViewById(R.id.content_title);

		topic_id = getIntent().getStringExtra("topic_id");
		head_pic = getIntent().getStringExtra("head_pic");
		movie_id = getIntent().getStringExtra("movie_id");
		title = getIntent().getStringExtra("title");
		
		et_text.setHint("#"+title+"#");
		et_text.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		
		String content = getIntent().getStringExtra("content");;
		if(content!=null){
			et_text.setText(content);
		}

		int width = dm.widthPixels;
		int height = width * 3 / 2;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		tv_image.setLayoutParams(params);

        ImageLoaderCharles.getInstance(SendMovieContent.this).addTask(head_pic, tv_image);
		content_title.setText(title);

        ImageLoaderCharles.getInstance(SendMovieContent.this).addTask(LiuLianApplication.current_user.getImage(), user_icon);
		user_name.setText(LiuLianApplication.current_user.getName());

		tv_image = (ImageView) findViewById(R.id.tv_image);
		publish = (TextView) findViewById(R.id.publish);
		user_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);

		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		tv_image.setOnClickListener(myOnClickListener);
		publish.setOnClickListener(myOnClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_image:
				Intent intent = new Intent();
				intent.putExtra("topic_id", topic_id);
				intent.putExtra("title", title);
				intent.setClass(activity_context, SearchMovie.class);
				startActivityForResult(intent, SERACHMUSIC);
				break;
			case R.id.publish:
				
				loadingDialog.show();
				if ("".equals(topic_id)) {

				} else {
					publish.setClickable(false);
					StringBuffer sbf =  new StringBuffer();
					sbf.append(et_text.getText().toString().trim()).append("\t"+"#"+title+"#");
					addContent(topic_id, sbf.toString(), movie_id);
				}
				break;
			case R.id.bt_back:
				onBackPressed();
				break;
			default:
				break;
			}
		}
	};

	//添加内容
	public void addContent(final String topic_id, String text_content, String movie_id) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		re.put("topic_id", topic_id);
		re.put("text_content", text_content);
		re.put("video_id", movie_id);
		Log.e("PathConst.URL_CONTENT_ADD", PathConst.URL_CONTENT_ADD + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey() + "&topic_id=" + topic_id + "&text_content=" + text_content
				+ "&video_id=" + movie_id);
		asyncHttp.get(PathConst.URL_CONTENT_ADD, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("地址：", json.toString());
					if (1 == json.optInt("flag")) {
						MyToast.makeText(SendMovieContent.this, "发布成功", 1).show();
						try {
							AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.SearchMovie"));
							AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.SendMovieContent"));
							AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.ContentList"));
							AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.SendContent"));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (json.has("egg")) {
							JSONArray json_arr = json.getJSONArray("egg");
							JSONObject egg_obj = json_arr.getJSONObject(0);
							Intent intent_send = new Intent();
							intent_send.setClass(activity_context, ShareMedalActivity.class);
							ShareContent share = new ShareContent();
							share.setId(egg_obj.getString("id"));
							share.setTitle(egg_obj.getString("name"));
							share.setSummary(egg_obj.getString("desc"));
							share.setImg_url(egg_obj.getString("icon"));
							share.setRedirect_url(CommonConst.GOV_URL);
							intent_send.putExtra("share", share);
							activity_context.startActivity(intent_send);
						}
						publish.setClickable(true);
						Intent intent = new Intent();
						intent.putExtra("topic_id", topic_id);
						intent.putExtra("title", title);
						intent.setClass(activity_context, ContentList.class);
						activity_context.startActivity(intent);
						SendMovieContent.this.finish();
					} else {
						publish.setClickable(true);
						MyToast.makeText(SendMovieContent.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SERACHMUSIC) {
			if (data != null) {
				String head_pic = data.getStringExtra("head_pic");
				movie_id = data.getStringExtra("movie_id");
				String name = data.getStringExtra("name");
                ImageLoaderCharles.getInstance(SendMovieContent.this).addTask(head_pic,tv_image);
				et_text.setText(name);
			}
		}
	}

}
