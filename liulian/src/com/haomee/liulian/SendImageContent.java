package com.haomee.liulian;

import java.io.File;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.liulian.upyun.UpYunException;
import com.haomee.liulian.upyun.UpYunUtils;
import com.haomee.liulian.upyun.Uploader;
import com.haomee.util.ViewUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SendImageContent extends BaseActivity {

	private ImageView tv_image;
	private TextView publish;
	private Context activity_context;
	private String head_pic;
	private String topic_id;
	private EditText et_text;
	private CircleImageView user_icon;
	private TextView user_name;
	private String path;
	private TextView content_title;
	private int width = 300;
	private int heigth = 200;
	private String title;
	public static final int SERACHMUSIC = 4;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_content_images);
		
		loadingDialog = new LoadingDialog(this);
		
		activity_context = this;

		topic_id = getIntent().getStringExtra("topic_id");
		content_title = (TextView) findViewById(R.id.content_title);
		title = getIntent().getStringExtra("title");
		content_title.setText(title);
		byte[] bis = getIntent().getByteArrayExtra("bitmap");
		Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
		width = getIntent().getIntExtra("width", 300);
		heigth = getIntent().getIntExtra("heigth", 200);
		path = getIntent().getStringExtra("path");
		new ImageUploadTask().execute(path);

		tv_image = (ImageView) findViewById(R.id.tv_image);
		publish = (TextView) findViewById(R.id.publish);
		et_text = (EditText) findViewById(R.id.et_text);
		user_icon = (CircleImageView) findViewById(R.id.user_icon);
		user_name = (TextView) findViewById(R.id.name);
		
		String content = getIntent().getStringExtra("content");;
		if(content!=null){
			et_text.setText(content);
		}

		et_text.setHint("#"+title+"#");
		et_text.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
        ImageLoaderCharles.getInstance(SendImageContent.this).addTask(LiuLianApplication.current_user.getImage(),user_icon);
		user_name.setText(LiuLianApplication.current_user.getName());
		user_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);

		
		
		
		ViewGroup.LayoutParams params = tv_image.getLayoutParams();
		if(width<ViewUtil.getScreenWidth(SendImageContent.this)- ViewUtil.dip2px(activity_context, 30)){
			
			params.width = ViewUtil.getScreenWidth(SendImageContent.this) - ViewUtil.dip2px(activity_context, 30);
		
		}else{
			
			params.width = width;
		}
		
		params.height = params.width;
		tv_image.setLayoutParams(params);
		
		tv_image.setImageBitmap(bitmap);

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
				intent.setClass(activity_context, SearchImage.class);
				startActivityForResult(intent, SERACHMUSIC);
				break;
			case R.id.publish:
				if ("".equals(topic_id)) {

				} else {
					publish.setClickable(false);
					if(head_pic!=null){
						loadingDialog.show();
						StringBuffer sbf =  new StringBuffer();
						sbf.append(et_text.getText().toString().trim()).append("\t"+"#"+title+"#");
						addContent(topic_id, sbf.toString(), head_pic, head_pic);
					}
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

	String SAVE_KEY = File.separator + "haomee" + File.separator + System.currentTimeMillis() + ".jpg";
	
	class ImageUploadTask extends AsyncTask<String, Void, String> {
		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间

		@Override
		protected String doInBackground(String... arg0) {
			String string = null;
			try {
				Log.e("----图片地址---", head_pic + "");
				String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
				String signature = UpYunUtils.signature(policy + "&" + TEST_API_KEY);
				string = Uploader.upload(policy, signature, BUCKET, arg0[0]);
			} catch (UpYunException e) {
				string = null;
				e.printStackTrace();
			}
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				head_pic = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
			} else {
				head_pic = null;
			}
		}
	}

	//添加内容
	public void addContent(final String topic_id, String text_content, String pic_content, String pic_thumb) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		re.put("topic_id", topic_id);
		re.put("text_content", text_content);
		re.put("pic_content", pic_content);
		re.put("pic_thumb", pic_thumb);
		re.put("pic_width", width + "");
		re.put("pic_height", heigth + "");
		asyncHttp.get(PathConst.URL_CONTENT_ADD, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("地址：", json.toString());
					if (1 == json.optInt("flag")) {
						MyToast.makeText(SendImageContent.this, "发送成功", 1).show();
						try {
							AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.SearchImage"));
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
						finish();
					} else {
						publish.setClickable(true);
						MyToast.makeText(SendImageContent.this, "发送失败", 1).show();
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
				topic_id = getIntent().getStringExtra("topic_id");
				content_title = (TextView) findViewById(R.id.content_title);
				title = getIntent().getStringExtra("title");
				content_title.setText(title);
				byte[] bis = getIntent().getByteArrayExtra("bitmap");
				Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
				width = getIntent().getIntExtra("width", 300);
				heigth = getIntent().getIntExtra("heigth", 200);
				path = getIntent().getStringExtra("path");
				new ImageUploadTask().execute(path);
				//new ImageUploadTask().execute(path);
				tv_image.setImageBitmap(bitmap);
			}
		}
	}

}
