package com.haomee.liulian;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.ShareContent;
import com.haomee.liulian.R.color;
import com.haomee.liulian.upyun.UpYunException;
import com.haomee.liulian.upyun.UpYunUtils;
import com.haomee.liulian.upyun.Uploader;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.haomee.view.SelectPicPopupWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.utils.LogUtil;

public class TopicAdd extends BaseActivity {
	private EditText topic_name, topic_content;
	private TextView topic_tip1, topic_tip2, topic_submit;
	private RelativeLayout tip;
	private Activity activity_context;
	private String rec_topic_id = "";
	private ImageView bt_back;
	private ImageView range_all_image, range_1_image, range_3_image, range_5_image, temp_image;
	private TextView range_all_text, range_1_text, range_3_text, range_5_text, temp_text;
	private int distance = 0;
	List<String> inspiration_arr;
	private SharedPreferences preference;
	SharedPreferences.Editor editor = null;
	private ImageView add_image;
	private List<ContentPicture> list_image;
	private ImageView iv_bg;
	private LinearLayout layout_bg;
	private LoadingBgTask loading__task;
	private SelectPicPopupWindow menuWindow;
	private String dir_temp;
	private File vFile;
	public File tempFile;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String path;
	private String top_bg = "";
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_add_new);
		activity_context = TopicAdd.this;
		loadingDialog = new LoadingDialog(this);
		dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
		preference = activity_context.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
		editor = preference.edit();
		inspiration_arr = new ArrayList<String>();
		initview();
		if (loading__task != null) {
			loading__task.cancel(true);
		}
		loading__task = new LoadingBgTask();
		loading__task.execute();
		LiuLianApplication.initLocation();
		
		
		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		
	}
	
	
	class LoadingBgTask extends AsyncTask<String, Integer, List<ContentPicture>> {
		@Override
		protected List<ContentPicture> doInBackground(String... url) {
			list_image = new ArrayList<ContentPicture>();
			try {
				String urlPath = PathConst.URL_BACK_IMG_LIST;
				LogUtil.e("地址", urlPath + "");
				try {
					JSONArray array_json = null;// 本地json数据
					// 有网取网络
					if (NetworkUtil.dataConnected(activity_context)) {
						array_json = NetworkUtil.getJsonArray(urlPath, null, 5000);// 5s延迟
					}
					if(array_json!=null && array_json.length()>0){
						for(int i= 0;i<array_json.length();i++){
							JSONObject json_object = array_json.getJSONObject(i);
							ContentPicture picture = new ContentPicture();
							picture.setSmall(json_object.optString("small_pic"));
							picture.setLarge(json_object.optString("big_pic"));
							list_image.add(picture);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list_image;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(List<ContentPicture> list_content) {
			
			if(list_image!=null && list_image.size()>0){
//				int width = ViewUtil.getScreenWidth(activity_context);
//				int height = width * 3/4;
//				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
//				iv_bg.setLayoutParams(params);
//				top_bg = list_image.get(0).getLarge();

                ImageLoaderCharles.getInstance(TopicAdd.this).addTask(list_image.get(0).getLarge(), iv_bg);

				if(layout_bg != null){
					LayoutInflater inflater = activity_context.getLayoutInflater();
					layout_bg.removeAllViews();
					for (ContentPicture picture : list_image) {
						 View item = inflater.inflate(R.layout.bg_item, null);
						 ImageView img = (ImageView) item.findViewById(R.id.iv_icon);


                        ImageLoaderCharles.getInstance(TopicAdd.this).addTask(picture.getLarge(), img);
						 item.setTag(picture);
						 item.setOnClickListener(itemListener);
						 layout_bg.addView(item);
					}
				}
			}
		}
	}
	
	
	OnClickListener itemListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (NetworkUtil.dataConnected(TopicAdd.this)) {
				ContentPicture picture = (ContentPicture) v.getTag();
//				int width = ViewUtil.getScreenWidth(activity_context);
//				int height = width/2;
//				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
//				iv_bg.setLayoutParams(params);
				top_bg = picture.getLarge();
                ImageLoaderCharles.getInstance(TopicAdd.this).addTask(picture.getLarge(), iv_bg);
			} else {
				MyToast.makeText(TopicAdd.this, TopicAdd.this.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			}
		}
	};
	


	@SuppressLint("WrongViewCast")
	public void initview() {	
		findViewById(R.id.layout_all).setOnClickListener(itemOnClick);
		findViewById(R.id.layout_1).setOnClickListener(itemOnClick);
		findViewById(R.id.layout_3).setOnClickListener(itemOnClick);
		findViewById(R.id.layout_5).setOnClickListener(itemOnClick);
		range_all_image = (ImageView) findViewById(R.id.range_all_image);
		temp_image = range_all_image;
		range_1_image = (ImageView) findViewById(R.id.range_1_image);
		range_3_image = (ImageView) findViewById(R.id.range_3_image);
		range_5_image = (ImageView) findViewById(R.id.range_5_image);
		range_all_text = (TextView) findViewById(R.id.range_all_distance);
		temp_text = range_all_text;
		range_1_text = (TextView) findViewById(R.id.range_1_distance);
		range_3_text = (TextView) findViewById(R.id.range_3_distance);
		range_5_text = (TextView) findViewById(R.id.range_5_distance);
		add_image = (ImageView) findViewById(R.id.add_image);
		iv_bg = (ImageView) findViewById(R.id.iv_bg);
		layout_bg = (LinearLayout) findViewById(R.id.layout_bg);
		add_image.setOnClickListener(itemOnClick);

		bt_back = (ImageView) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(itemOnClick);
		tip = (RelativeLayout) findViewById(R.id.tip);
		topic_name = (EditText) findViewById(R.id.topic_name);
		//topic_name.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		topic_content = (EditText) findViewById(R.id.topic_content);
		topic_content.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		topic_content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		topic_tip1 = (TextView) findViewById(R.id.topic_tip1);
		topic_tip2 = (TextView) findViewById(R.id.topic_tip2);
		topic_tip2.setText(Html.fromHtml("<u>或去该话题发言</u>"));
		topic_tip2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TopicAdd.this, ContentList.class);
				intent.putExtra("topic_id", rec_topic_id);
				startActivity(intent);
			}
		});
		
		
		
		topic_submit = (TextView) findViewById(R.id.topic_submit);
		topic_submit.setOnClickListener(itemOnClick);
		

		topic_name.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String hint = "";
				if (!hasFocus) {//失去焦点
					hint = topic_name.getTag().toString();
					topic_name.setHint(hint);
					String url = PathConst.URL_CHECK_TOPIC_EXIST + "&title=" + URLEncoder.encode(topic_name.getText().toString().trim()) + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude;
					AsyncHttpClient asyncHttp = new AsyncHttpClient();
					asyncHttp.get(url, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String arg0) {
							super.onSuccess(arg0);
							try {
								JSONObject json = new JSONObject(arg0);
								if (1 == json.optInt("flag")) {
									tip.setVisibility(View.GONE);
								} else if (2 == json.optInt("flag")) {
									tip.setVisibility(View.VISIBLE);
									//名字重复，显示推荐名字
									if (json.optInt("status") == 0) {
										rec_topic_id = json.optString("topic_id");
										if (rec_topic_id.equals("0")) {
											tip.setVisibility(View.GONE);
										} else {
											tip.setVisibility(View.VISIBLE);
										}
										topic_tip2.setVisibility(View.VISIBLE);
										topic_tip1.setText("该话题已存在，建议修改为" + json.optString("rec_name"));
									} else if (json.optInt("status") == 1) {

										topic_tip2.setVisibility(View.GONE);
										topic_tip1.setText("您发布的话题已存在或已删除，请与系统管理员联系！");
									} else if (json.optInt("status") == 2) {
										topic_tip2.setVisibility(View.GONE);
										topic_tip1.setText("您发布的话题已存在，目前正在审核中，请稍后到该话题内发布内容");
									} else {
										return;
									}
								} else if (0 == json.optInt("flag")) {
									tip.setVisibility(View.GONE);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					hint = topic_name.getHint().toString();
					topic_name.setTag(hint);
					topic_name.setHint("");
				}
			}
		});
	}

	private OnClickListener itemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_image:
				menuWindow = new SelectPicPopupWindow(TopicAdd.this, itemOnClick);
				menuWindow.showAtLocation(TopicAdd.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.btn_take_photo:
				menuWindow.dismiss();
				vFile = new File(dir_temp + "user_icon_temp.jpg");
				Uri uri = Uri.fromFile(vFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, PHOTOHRAPH);
				break;
			case R.id.btn_pick_photo:
				menuWindow.dismiss();
				Intent intent2 = new Intent();
				intent2.setClass(TopicAdd.this, AlbumActivity.class);
				startActivityForResult(intent2, CROPIMAGES);
				break;
			case R.id.layout_1:
				temp_image.setImageResource(R.drawable.topic_button_frame);
				temp_text.setTextColor(getResources().getColor(color.main_color_gray));
				range_1_image.setImageResource(R.drawable.topic_button_frame_pressed);
				range_1_text.setTextColor(getResources().getColor(color.main_color));
				temp_image = range_1_image;
				temp_text = range_1_text;
				distance = 1;
				break;
			case R.id.layout_3:
				temp_image.setImageResource(R.drawable.topic_button_frame);
				temp_text.setTextColor(getResources().getColor(color.main_color_gray));
				range_3_image.setImageResource(R.drawable.topic_button_frame_pressed);
				range_3_text.setTextColor(getResources().getColor(color.main_color));
				temp_image = range_3_image;
				temp_text = range_3_text;
				distance = 3;
				break;
			case R.id.layout_5:
				temp_image.setImageResource(R.drawable.topic_button_frame);
				temp_text.setTextColor(getResources().getColor(color.main_color_gray));
				range_5_image.setImageResource(R.drawable.topic_button_frame_pressed);
				range_5_text.setTextColor(getResources().getColor(color.main_color));
				temp_image = range_5_image;
				temp_text = range_5_text;
				distance = 5;
				break;
			case R.id.layout_all:
				temp_image.setImageResource(R.drawable.topic_button_frame);
				temp_text.setTextColor(getResources().getColor(color.main_color_gray));
				range_all_image.setImageResource(R.drawable.topic_button_frame_pressed);
				range_all_text.setTextColor(getResources().getColor(color.main_color));
				temp_image = range_all_image;
				temp_text = range_all_text;
				distance = 0;
				break;
			case R.id.bt_back:
				activity_context.finish();
				break;
			case R.id.topic_submit:
				if (!NetworkUtil.dataConnected(activity_context)) {
					MyToast.makeText(activity_context, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					return;
				}
				if (topic_content.getText().toString().equals("")) {
					MyToast.makeText(activity_context, "请输入内容！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (topic_name.getText().toString().equals("")) {
					MyToast.makeText(activity_context, "请输入标题！", Toast.LENGTH_SHORT).show();
					return;
				}
				@SuppressWarnings("deprecation")
				String url = PathConst.URL_ADD_TOPIC
				+ "&uid=" + LiuLianApplication.current_user.getUid() + "&title=" + URLEncoder.encode(topic_name.getText().toString().trim()) + "&desc="
						+ URLEncoder.encode(topic_content.getText().toString().trim()) + "&view=" + distance + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude + "&back_img="+top_bg;
				LogUtil.e("添加话题", url + "");
				RequestParams re = new RequestParams();
				re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
				re.put("Luid", LiuLianApplication.current_user.getUid());
				AsyncHttpClient asyncHttp = new AsyncHttpClient();
				asyncHttp.get(url, re,new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String arg0) {   
						super.onSuccess(arg0);
						try {
							JSONObject json = new JSONObject(arg0);
							if (1 == json.optInt("flag")) {
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
								} else {
									MyToast.makeText(activity_context, "提交成功", Toast.LENGTH_SHORT).show();
								}
								activity_context.finish();
							} else if (2 == json.optInt("flag")) {
								//名字重复，显示推荐名字
								tip.setVisibility(View.VISIBLE);
								if (json.optInt("status") == 0) {
									rec_topic_id = json.optString("topic_id");
									if (rec_topic_id.equals("0")) {
										tip.setVisibility(View.GONE);
									} else {
										tip.setVisibility(View.VISIBLE);
									}
									topic_tip2.setVisibility(View.VISIBLE);
									topic_tip1.setText("该话题已存在，建议修改为" + json.optString("rec_name"));
								} else if (json.optInt("status") == 1) {
									topic_tip2.setVisibility(View.GONE);
									topic_tip1.setText("您发布的话题已存在或已删除，请与系统管理员联系！");
								} else if (json.optInt("status") == 2) {
									topic_tip2.setVisibility(View.GONE);
									topic_tip1.setText("您发布的话题已存在，目前正在审核中，请稍后到该话题内发布内容");
								} else {
									return;
								}

							} else if (0 == json.optInt("flag")) {
								MyToast.makeText(activity_context, json.optString("msg"), Toast.LENGTH_SHORT).show();
								tip.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				break;
			}
		}
	};
	

	//裁剪
	public void startCrop(String path) {
		Intent intent = new Intent();
		intent.putExtra("path", path);
		intent.setClass(this, ImageCropActivity.class);
		startActivityForResult(intent, CROPIMAGES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOHRAPH) {
			if (vFile != null && vFile.exists()) {
				startCrop(Uri.fromFile(vFile).getPath());
			}
		} else if (requestCode == CROPIMAGES) {
			if (data != null) {
				path = data.getStringExtra("path");
				loadingDialog.show();
				new ImageUploadTask().execute(path);
			}
			
		}
	}

	class ImageUploadTask extends AsyncTask<String, Void, String> {

		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; //过期时间，必须大于当前时间
		String SAVE_KEY = File.separator + "haomee" + File.separator + System.currentTimeMillis() + ".jpg";

		@Override
		protected String doInBackground(String... arg0) {
			String string = null;
			try {
				String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
				String signature = UpYunUtils.signature(policy + "&" + TEST_API_KEY);
				string = Uploader.upload(policy, signature, BUCKET, arg0[0]);
			} catch (UpYunException e) {
				e.printStackTrace();
			}
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result != null) {
				loadingDialog.dismiss();
				top_bg = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
                ImageLoaderCharles.getInstance(TopicAdd.this).addTask(top_bg,iv_bg);
			} else {
				top_bg = null;
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	};
}
