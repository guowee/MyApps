package com.hipad.smarthome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.Response;
import com.hipad.smart.json.util.GsonInstance;
import com.hipad.smart.user.User;
import com.hipad.smart.util.StringUtil;
import com.hipad.smarthome.utils.BitmapUtil;

public class UserInfoActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "UserInfoActivity";
	
	private RelativeLayout viewlayout;
	private EditText nick_name, name, birth, phone, maibox, address, carrer, commpany;
	private RadioGroup mSexy;
	private RadioButton sex_male, sex_female;
	private ImageButton mConfirm;
	private ImageView mHead;
	private String sexyStr = null;
	private static final int DATA_PICKER_ID = 1;
	private Intent intent;
	
	private Bitmap profile = null;
	
	private User user;
	
	private ResponseResultHandler<Response> mUpdateResultHandler = new ResponseResultHandler<Response>() {
		
		@Override
		public void handle(boolean timeout, Response response) {
			dialogDismiss();
			if(timeout || null == response)	showToastShort("修改失败， 请重试！");
			
			if(response.isSuccessful()){				
				MyApplication.user = user;
				application.saveUserInfo();
				
				if (profile != null) {
					saveHead(profile);
				}
				
				showToastShort("修改成功！");
				Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
			}else{
				showToastShort(response.getMsg());
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		intent = getIntent();
//		user = new User();
		
		getView();
		initView();
	}
	
	private void getView(){
		viewlayout = (RelativeLayout) findViewById(R.id.viewlayout);

		nick_name = (EditText) findViewById(R.id.edit_nickname);
		name = (EditText) findViewById(R.id.edit_name);
		birth = (EditText) findViewById(R.id.edit_birth);
		phone = (EditText) findViewById(R.id.edit_phone);
		maibox = (EditText) findViewById(R.id.edit_mailbox);
		address = (EditText) findViewById(R.id.edit_address);
		carrer = (EditText) findViewById(R.id.edit_carrer);
		commpany = (EditText) findViewById(R.id.edit_company);

		mSexy = (RadioGroup) findViewById(R.id.edit_sex);
		sex_male = (RadioButton) findViewById(R.id.sex_male);
		sex_female = (RadioButton) findViewById(R.id.sex_female);

		mHead = (ImageView) findViewById(R.id.head_view);
		mConfirm = (ImageButton) findViewById(R.id.btn_confirm);
		mHead.setOnClickListener(new HeadSetListener());
		mConfirm.setOnClickListener(this);

	}

	private void initView() {
		File file = new File(application.getSettings().getUserProfileCachePath(MyApplication.user));
		if (file.exists()) {
			Bitmap profile = BitmapFactory.decodeFile(file.getAbsolutePath());
			mHead.setImageBitmap(profile);
		}
		
		String nickName = MyApplication.user.getNickName();
		if (!StringUtil.isNullOrEmpty(nickName) ) {
			nick_name.setText(nickName);
		}

		String strName = MyApplication.user.getName();
		if (!StringUtil.isNullOrEmpty(strName)) {
			name.setText(strName);
		}
		
		String birthday = MyApplication.user.getBirthday();
		if (!StringUtil.isNullOrEmpty(birthday)) {
			birth.setText(birthday);
		}
		
		String gender = MyApplication.user.getGender();
		if (!StringUtil.isNullOrEmpty(gender)) {
			if(getString(R.string.string_sex_male).equals(gender)){
				sex_male.setChecked(true);
				sexyStr = getString(R.string.string_sex_male);
			}else if(getString(R.string.string_sex_female).equals(gender)){
				sex_female.setChecked(true);
				sexyStr = getString(R.string.string_sex_female);
			}
		}
		
		String mobile = MyApplication.user.getMobile();
		if (!StringUtil.isNullOrEmpty(mobile)) {
			phone.setFocusable(false);
			phone.setText(mobile);
		}
		
		String email = MyApplication.user.getEmail();
		if (!StringUtil.isNullOrEmpty(email)) {
			maibox.setText(email);
		}
		
		String addr = MyApplication.user.getAddString();
		if (!StringUtil.isNullOrEmpty(addr)) {
			address.setText(addr);
		}
		
		String job = MyApplication.user.getJob();
		if (!StringUtil.isNullOrEmpty(job)) {
			carrer.setText(job);
		}
		
		String strCompany = MyApplication.user.getCompany();
		if (!StringUtil.isNullOrEmpty(strCompany)) {
			commpany.setText(strCompany);
		}
		
		birth.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					showDialog(DATA_PICKER_ID);
				}
			}
		});

		birth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(DATA_PICKER_ID);
			}
		});

		mSexy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == sex_male.getId()) {
					sexyStr = getString(R.string.string_sex_male);
				} else if (checkedId == sex_female.getId()) {
					sexyStr = getString(R.string.string_sex_female);
				}
			}
		});
	}

	class HeadSetListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent mIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(mIntent, 1);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			Cursor cursor = cr.query(uri, null, null, null, null);
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				long thumbNailsId = cursor.getLong(cursor.getColumnIndex("_ID"));
				Bitmap profileOrigin = MediaStore.Images.Thumbnails.getThumbnail(cr, thumbNailsId, Images.Thumbnails.MICRO_KIND, null);
				
				profile = BitmapUtil.clipARoundPortrait(profileOrigin);
				profileOrigin.recycle();
				mHead.setImageBitmap(profile);
			}
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void saveHead(Bitmap bitmap) {
		File file = new File(application.getSettings().getUserProfileCachePath(MyApplication.user));
		
		FileOutputStream out = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
				out.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


		@Override
		public void onClick(View arg0) {		

			String nick_name_str, name_str = null, birth_str = null, phone_str = null, email_str = null, address_str = null,carrer_str = null,commpany_str = null;
			
			nick_name_str = nick_name.getText().toString();
			name_str = name.getText().toString();
			birth_str = birth.getText().toString();
			phone_str = phone.getText().toString();
			email_str = maibox.getText().toString();
			address_str = address.getText().toString();
			carrer_str = carrer.getText().toString();
			commpany_str = commpany.getText().toString();

			if (nick_name_str == null) {
				showToastShort(getResources().getString(R.string.string_nickname_null));
				return;
			}
			
			if (birth_str == null) {
				showToastShort(getResources().getString(R.string.string_birth_null));
				return;
			}
			
			if (sexyStr == null) {
				showToastShort(getResources().getString(R.string.string_sex_null));
				return;
			}
			
			if (phone_str == null) {
				showToastShort(getResources().getString(R.string.string_phone_null));
				return;
			}
			
			if (address_str == null) {
				showToastShort(getResources().getString(R.string.string_address_null));
				return;
			}
			
			if (carrer_str == null) {
				showToastShort(getResources().getString(R.string.string_carrer_null));
				return;
			}
			
			if (commpany_str == null) {
				showToastShort(getResources().getString(R.string.string_company_null));
				return;
			}
			if (name_str.isEmpty() || !name_str.matches("[\u4e00-\u9fa5\\w]+")) {
				showToastShort(getResources().getString(R.string.string_name_null));
				return;
			}else{
				if(name_str.length() < 2 || name_str.length() >16){
					showToastShort(getResources().getString(R.string.string_name_illege));
					return;
				}
			}
			
			if (phone_str.isEmpty() || phone_str.length() != 11) {
				showToastShort(getResources().getString(R.string.string_phone_illege));
				return;
			}
			
			/**
			name_str 用户名
			birth_str 生日
			phone_str 手机号码
			address_str 地址
			carrer_str 职业
			commpany_str 公司
			maibox_str 邮箱 
			nick_name_str 昵称
			sexyStr 性别
			*/
			user = GsonInstance.get().fromJson(GsonInstance.get().toJson(MyApplication.user), User.class); // keep current user's all info 
			user.setNickName(nick_name_str);
			user.setName(name_str);
			user.setBirthday(birth_str);
			user.setGender(sexyStr);
			user.setMobile(phone_str);
			user.setEmail(email_str);
			user.setAddress(address_str);
			user.setJob(carrer_str);
			user.setCompany(commpany_str);
			
			service.updateUserInfo(user, mUpdateResultHandler);
			dialogShow(this, null);
		}

	DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String date_y = year + "";
			String date_m = (monthOfYear + 1) + "";
			String date_d = dayOfMonth + "";
			birth.setText(date_y + "/" + date_m + "/" + date_d);
		}
	};

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATA_PICKER_ID:
			Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
			t.setToNow(); // 取得系统时间。
			int year = t.year;
			int month = t.month;
			int day = t.monthDay;
			Log.d(TAG, "year:" + year + " month:" + month + " day:" + day);
			return new DatePickerDialog(this, onDateSetListener, year, month, day);
		}

		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
