package com.haomee.liulian;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.haomee.entity.Image;
import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.easemob.util.NetUtils;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.liulian.upyun.UpYunException;
import com.haomee.liulian.upyun.UpYunUtils;
import com.haomee.liulian.upyun.Uploader;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.haomee.view.SelectPicPopupWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.taomee.view.calendar.ArrayWheelAdapter;
import com.taomee.view.calendar.OnWheelChangedListener;
import com.taomee.view.calendar.WheelView;

public class UserInfoActivity extends BaseActivity {

	private ImageView iv_icon;
	private EditText et_name;
	private TextView tv_submit;
	private SelectPicPopupWindow menuWindow;
	private String dir_temp;
	private File vFile;
	public File tempFile;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	private static final int REQUEST_CODE_PICK_IMAGE = 5;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String head_pic;
	private int Choice_Sex = 0;
	private int from_activity = 0;
	private String path;

	private WheelView yearWV = null;
	private WheelView monthWV = null;
	private WheelView dayWV = null;
	// 滚轮上的数据，字符串数组
	String[] yearArrayString = null;
	String[] dayArrayString = null;
	String[] monthArrayString = null;
	Calendar c = null;
	int year;
	int month;
	private TextView et_date, et_astro;
	private Dialog dialog;
	private String str_date = "";
	private List<String> name_list;
	private TextView tv_random;
	private LoadingDialog loadingDialog;

	private TextView tv_canle;
	private TextView tv_ok;

	private int sex_selected_id = 0;
	private int sex_intend_selected_id = 0;
	private RadioGroup sex_group, sex_intend_group;

	private RadioButton sex_female, sex_male, sex_female_intend, sex_male_intend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LiuLianApplication.initLocation();
		setContentView(R.layout.activity_userinfo);

		loadingDialog = new LoadingDialog(this);

		from_activity = getIntent().getIntExtra("from_activity", 0);

		dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (from_activity == 3) {
					Intent intent = new Intent();
					intent.setClass(UserInfoActivity.this, LoginPageActivity.class);
					UserInfoActivity.this.startActivity(intent);
					UserInfoActivity.this.finish();
				} else {
					onBackPressed();
				}
			}
		});

		iv_icon = (ImageView) findViewById(R.id.iv_icon);

		sex_female = (RadioButton) findViewById(R.id.sex_female);
		sex_male = (RadioButton) findViewById(R.id.sex_male);
		sex_male_intend = (RadioButton) findViewById(R.id.sex_male_intend);
		sex_female_intend = (RadioButton) findViewById(R.id.sex_female_intend);

		sex_group = (RadioGroup) this.findViewById(R.id.self_sex);
		sex_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				sex_selected_id = arg0.getCheckedRadioButtonId() == R.id.sex_female ? 0 : 1;

				set_data_sex();

			}
		});
		sex_intend_group = (RadioGroup) this.findViewById(R.id.self_sex_intend);
		sex_intend_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				sex_intend_selected_id = arg0.getCheckedRadioButtonId() == R.id.sex_female_intend ? 0 : 1;
				set_data_sex();
			}
		});
		et_name = (EditText) findViewById(R.id.et_name);
		tv_submit = (TextView) findViewById(R.id.tv_submit);
		et_date = (TextView) findViewById(R.id.et_date);
		et_astro = (TextView) findViewById(R.id.et_astro);
		tv_random = (TextView) findViewById(R.id.tv_random);

		iv_icon.setOnClickListener(btItemClick);
		tv_submit.setOnClickListener(btItemClick);
		tv_random.setOnClickListener(btItemClick);

		et_date.setOnClickListener(btItemClick);

		initData();

		Init_Name();
	}

	// 随机用户名
	public void Init_Name() {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(PathConst.URL_RAND_UAERNAME, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					name_list = new ArrayList<String>();
					JSONArray array_name = new JSONArray(arg0);
					if (array_name.length() > 0) {

						for (int i = 0; i < array_name.length(); i++) {

							name_list.add(array_name.getString(i));

						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init_dateview(Window view) {

		yearWV = (WheelView) view.findViewById(R.id.time_year);
		monthWV = (WheelView) view.findViewById(R.id.time_month);
		dayWV = (WheelView) view.findViewById(R.id.time_day);
		tv_canle = (TextView) view.findViewById(R.id.tv_canle);
		tv_ok = (TextView) view.findViewById(R.id.tv_ok);

		// 设置每个滚轮的行数
		yearWV.setVisibleItems(5);
		monthWV.setVisibleItems(5);
		dayWV.setVisibleItems(5);

		// 设置滚轮的标签
		yearWV.setLabel("年");
		monthWV.setLabel("月");
		dayWV.setLabel("日");

		yearWV.setCyclic(true);
		monthWV.setCyclic(true);
		dayWV.setCyclic(true);

		// 得到相应的数组
		yearArrayString = getYEARArray(1950);
		monthArrayString = getDayArray(12);
		// 获取当前系统时间
		c = Calendar.getInstance();
		// 给滚轮提供数据
		yearWV.setAdapter(new ArrayWheelAdapter<String>(yearArrayString));
		monthWV.setAdapter(new ArrayWheelAdapter<String>(monthArrayString));

		yearWV.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				// 获取年和月
				year = Integer.parseInt(yearArrayString[yearWV.getCurrentItem()]);
				month = Integer.parseInt(monthArrayString[monthWV.getCurrentItem()]);
				// 根据年和月生成天数数组
				dayArrayString = getDayArray(getDay(year, month));
				// 给天数的滚轮设置数据
				dayWV.setAdapter(new ArrayWheelAdapter<String>(dayArrayString));
				// 防止数组越界
				if (dayWV.getCurrentItem() >= dayArrayString.length) {
					dayWV.setCurrentItem(dayArrayString.length - 1);
				}
			}
		});

		// 当月变化时显示的时间
		monthWV.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				// 获取年和月
				year = Integer.parseInt(yearArrayString[yearWV.getCurrentItem()]);
				month = Integer.parseInt(monthArrayString[monthWV.getCurrentItem()]);
				// 根据年和月生成天数数组
				dayArrayString = getDayArray(getDay(year, month));
				// 给天数的滚轮设置数据
				dayWV.setAdapter(new ArrayWheelAdapter<String>(dayArrayString));
				// 防止数组越界
				if (dayWV.getCurrentItem() >= dayArrayString.length) {
					dayWV.setCurrentItem(dayArrayString.length - 1);
				}
			}
		});

		// 把当前系统时间显示为滚轮默认时间
		if (et_date.getText().toString().equals("")) {
			setOriTime();
		} else {
			if (LiuLianApplication.current_user != null && LiuLianApplication.current_user.getBirthday() != null && !"".equals(LiuLianApplication.current_user.getBirthday())) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						String[] date = LiuLianApplication.current_user.getBirthday().split("[-]");
						year = Integer.parseInt(date[0]);
						month = Integer.parseInt(date[1]);
						yearWV.setCurrentItem(getNumData(date[0], yearArrayString));
						monthWV.setCurrentItem(getNumData(date[1], monthArrayString));
						dayArrayString = getDayArray(getDay(year, month));

						int day = Integer.parseInt(date[2]);
						dayWV.setAdapter(new ArrayWheelAdapter<String>(dayArrayString));
						dayWV.setCurrentItem(day - 1);
					}
				}, 200);

			} else {

				setOriTime();

			}

		}

		tv_canle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

				Time time_now = new Time("GMT+8");
				time_now.setToNow();
				/*
				 * int year_now = time.year; int month_now = time.month+1; int
				 * day_now = time.monthDay;
				 */

				Time time_selected = new Time("GMT+8");
				time_selected.year = Integer.parseInt(yearArrayString[yearWV.getCurrentItem()]);
				time_selected.month = Integer.parseInt(monthArrayString[monthWV.getCurrentItem()]) - 1;
				time_selected.monthDay = Integer.parseInt(dayArrayString[dayWV.getCurrentItem()]);

				/*
				 * int year_selected =
				 * Integer.parseInt(yearArrayString[yearWV.getCurrentItem()]);
				 * int month_selected =
				 * Integer.parseInt(monthArrayString[monthWV.getCurrentItem()]);
				 * int day_selected =
				 * Integer.parseInt(dayArrayString[dayWV.getCurrentItem()]);
				 */

				if (time_selected.after(time_now)) {
					Toast.makeText(UserInfoActivity.this, "日期选择不正确", 1).show();
				} else {
					showDate();
				}

			}
		});
	}

	// 设定初始时间
	void setOriTime() {
		yearWV.setCurrentItem(40);
		monthWV.setCurrentItem(3);
		dayArrayString = getDayArray(getDay(year, month));
		dayWV.setAdapter(new ArrayWheelAdapter<String>(dayArrayString));
		dayWV.setCurrentItem(getNumData(c.get(Calendar.DAY_OF_MONTH) + "", dayArrayString));
		// 初始化显示的时间
		// showDate();
	}

	// 显示时间
	void showDate() {
		createDate(yearArrayString[yearWV.getCurrentItem()], monthArrayString[monthWV.getCurrentItem()], dayArrayString[dayWV.getCurrentItem()]);
	}

	// 生成时间
	void createDate(String year, String month, String day) {
		// String dateStr = year + "年" + month + "月" + day + "日";
		str_date = year + "-" + month + "-" + day;
		et_date.setText(str_date);
		getAstro(month, day);
	}

	public void getAstro(String month, String day) {
		int m = Integer.parseInt(month);
		int d = Integer.parseInt(day);
		String[] starArr = { "魔羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座" };
		int[] DayArr = { 22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22 }; // 两个星座分割日
		int index = m;
		// 所查询日期在分割日之前，索引-1，否则不变
		if (d <= DayArr[m - 1]) {
			index = index - 1;
		}

		if (index >= 12) {
			index = 0;
		}

		et_astro.setText("( " + starArr[index] + " )");

	}

	// 在数组Array[]中找出字符串s的位置
	int getNumData(String s, String[] Array) {
		int num = 0;
		if (s.startsWith("0")) {
			s = s.substring(1);
		}
		for (int i = 0; i < Array.length; i++) {
			if (s.equals(Array[i])) {
				num = i;
				break;
			}
		}
		return num;
	}

	// 根据当前年份和月份判断这个月的天数
	public int getDay(int year, int month) {
		int day;
		if (year % 4 == 0 && year % 100 != 0) { // 闰年
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				day = 31;
			} else if (month == 2) {
				day = 29;
			} else {
				day = 30;
			}
		} else { // 平年
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				day = 31;
			} else if (month == 2) {
				day = 28;
			} else {
				day = 30;
			}
		}
		return day;
	}

	// 根据数字生成一个字符串数组
	public String[] getDayArray(int day) {
		String[] dayArr = new String[day];
		for (int i = 0; i < day; i++) {
			dayArr[i] = i + 1 + "";
		}
		return dayArr;
	}

	// 根据数字生成一个字符串数组
	public String[] getHMArray(int day) {
		String[] dayArr = new String[day];
		for (int i = 0; i < day; i++) {
			dayArr[i] = i + "";
		}
		return dayArr;
	}

	// 根据初始值start得到一个字符数组，自start起至今年
	public String[] getYEARArray(int start) {

		Time time = new Time("GMT+8");
		time.setToNow();
		int year_now = time.year;

		int size = year_now - start + 1;
		if (size <= 0) {
			return null;
		}

		String[] dayArr = new String[size];

		for (int i = 0; i < size; i++) {
			dayArr[i] = (start + i) + "";
		}
		return dayArr;
	}

	public void initData() {

		ImageLoaderCharles.getInstance(UserInfoActivity.this).addTask(LiuLianApplication.current_user.getImage(), iv_icon);
		iv_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
		et_name.setText(LiuLianApplication.current_user.getName());
		et_date.setText(LiuLianApplication.current_user.getBirthday());

		if (LiuLianApplication.current_user.getStar() == null || LiuLianApplication.current_user.getStar().equals("")) {
			et_astro.setText("摩羯座");
		} else {
			et_astro.setText("( " + LiuLianApplication.current_user.getStar() + " )");
		}

		Choice_Sex = LiuLianApplication.current_user.getSex();
		switch (Choice_Sex) {

		case 1:
			sex_female.setChecked(true);
			sex_male_intend.setChecked(true);
			break;
		case 2:
			sex_female.setChecked(true);
			sex_female_intend.setChecked(true);
			break;
		case 3:
			sex_male.setChecked(true);
			sex_female_intend.setChecked(true);
			break;
		case 4:
			sex_male.setChecked(true);
			sex_male_intend.setChecked(true);
			break;
		default:
			break;
		}

	}

	public OnClickListener btItemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_icon:
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, btItemClick);
				menuWindow.showAtLocation(UserInfoActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.btn_take_photo: {
				menuWindow.dismiss();
				vFile = new File(dir_temp + "user_icon_temp.jpg");
				Uri uri = Uri.fromFile(vFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, PHOTOHRAPH);
				break;
			}
			case R.id.btn_pick_photo:
				menuWindow.dismiss();
				/*
				 * Intent intent2 = new Intent();
				 * intent2.setClass(UserInfoActivity.this, AlbumActivity.class);
				 * startActivityForResult(intent2, CROPIMAGES);
				 */

				try {
					Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent2, PHOTOZOOM);
				} catch (Exception e) {
					// Toast.makeText(context, "打开相册失败",
					// Toast.LENGTH_SHORT).show();
					Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
					startActivityForResult(intent2, PHOTOZOOM);
				}
				break;
			case R.id.tv_submit:
				if ("".equals(et_name.getText().toString().trim())) {// 用户名
					MyToast.makeText(UserInfoActivity.this, "请输入用户名", 1).show();
					return;
				}

				if (head_pic == null) {
					head_pic = LiuLianApplication.current_user.getImage();
				}

				if (head_pic == null || head_pic.equals("")) {
					MyToast.makeText(UserInfoActivity.this, "请上传图像", 1).show();
					return;
				}

				String birthday = et_date.getText().toString().trim();
				if (birthday.equals("")) {// 日期
					MyToast.makeText(UserInfoActivity.this, "请选择出生日期", 1).show();
					return;
				}

				if (NetUtils.hasDataConnection(UserInfoActivity.this)) {
					loadingDialog.show();
					updateUserInfo(LiuLianApplication.current_user.getUid(), LiuLianApplication.current_user.getAccesskey(), et_name.getText().toString().trim(), Choice_Sex, head_pic, birthday);
				} else {
					MyToast.makeText(UserInfoActivity.this, "请检查网络设置", 1).show();
				}

				break;
			case R.id.et_date:
				dialog = new Dialog(UserInfoActivity.this, R.style.Transparent_);
				dialog.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.date_dialog);
				try {
					init_dateview(window);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.tv_random:
				StatService.onEvent(UserInfoActivity.this, "personal_setting_random", "个人设置页面随机点击次数", 1);

				try {
					et_name.setText(name_list.get((int) (Math.random() * 100)));
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
		}
	};

	private void set_data_sex() {
		if (sex_selected_id == 0 && sex_intend_selected_id == 0) {
			// lesbian
			Choice_Sex = 2;
			iv_icon.setBackgroundResource(CommonConst.user_sex[2]);
		} else if (sex_selected_id == 0 && sex_intend_selected_id == 1) {
			// 直女
			Choice_Sex = 1;
			iv_icon.setBackgroundResource(CommonConst.user_sex[1]);
		} else if (sex_selected_id == 1 && sex_intend_selected_id == 0) {
			// 直男
			Choice_Sex = 3;
			iv_icon.setBackgroundResource(CommonConst.user_sex[3]);
		} else if (sex_selected_id == 1 && sex_intend_selected_id == 1) {
			// gay
			Choice_Sex = 4;
			iv_icon.setBackgroundResource(CommonConst.user_sex[4]);
		}
	}

	// 裁剪
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
				if (!NetworkUtil.dataConnected(UserInfoActivity.this)) {
					MyToast.makeText(UserInfoActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					loadingDialog.dismiss();
					return;
				} else {
					new ImageUploadTask().execute(path);
				}

			}
		} else if (requestCode == PHOTOZOOM) {
			if (data != null) {
				// 读取相册缩放图片
				Uri originalUri = data.getData();
				if (originalUri != null) {

					try {
						String[] proj = { MediaStore.Images.Media.DATA };
						@SuppressWarnings("deprecation")
						Cursor cursor = managedQuery(originalUri, proj, null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(column_index);
						startCrop(path);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

	class ImageUploadTask extends AsyncTask<String, Void, String> {

		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间
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
				head_pic = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
				ImageLoaderCharles.getInstance(UserInfoActivity.this).addTask(head_pic, iv_icon);
			} else {
				head_pic = null;
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	// 修改个人信息
	public void updateUserInfo(String uid, String accesskey, final String username, final int sex, final String head_pic, String birthday) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", uid);
		re.put("Luid", uid);
		re.put("accesskey", accesskey);
		re.put("username", username);
		re.put("sex", sex + "");
		re.put("head_pic", head_pic);
		re.put("city", LiuLianApplication.city);
		re.put("birthday", birthday);
		Log.e("地址：", PathConst.URL_EDIT_USER_INFO + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey() + "&username=" + URLEncoder.encode(LiuLianApplication.current_user.getName())
				+ "&sex=" + sex + "&head_pic=" + head_pic + "&birthday=" + birthday);
		asyncHttp.get(PathConst.URL_EDIT_USER_INFO, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						// MyToast.makeText(UserInfoActivity.this, "修改成功",
						// 1).show();
						Intent intent = new Intent();
						if (from_activity == 0) {
							intent.setClass(UserInfoActivity.this, MainActivity.class);
							UserInfoActivity.this.startActivity(intent);
							UserInfoActivity.this.finish();
						} else {
							Users user = new Users();
							JSONObject json_user = json.getJSONObject("user");
							user.setUid(json_user.getString("id"));
							user.setName(json_user.getString("username"));
							user.setImage(json_user.getString("head_pic"));
							user.setSex(json_user.optInt("sex"));
							user.setPhone(json_user.getString("mobile"));
							user.setAccesskey(json_user.optString("accesskey"));
							user.setHx_username(json_user.optString("hx_username"));
							user.setHx_password(json_user.optString("hx_password"));
							user.setCity(json_user.optString("city"));
							user.setContent_num(Integer.parseInt(json_user.optString("content_num")));
							user.setTopic_num(Integer.parseInt(json_user.optString("topic_num")));
							user.setBirthday(json_user.optString("birthday"));
							user.setAge(json_user.optString("age"));
							user.setStar(json_user.optString("star"));
							user.setSignature(json_user.optString("signature"));
							user.setIs_new(0);
							LiuLianApplication.current_user = user;
							LiuLianApplication.getInstance().saveLoginedUser();
							finish();
						}
					} else {
						MyToast.makeText(UserInfoActivity.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}
}
