package com.hipad.tracker;

import com.hipad.tracker.widget.TuneWheel;
import com.hipad.tracker.widget.TuneWheel.OnValueChangeListener;
import com.hipad.tracker.widget.WheelView;
import com.hipad.tracker.widget.WheelView.OnWheelItemSelectListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author guowei
 *
 */
public class BabyInformSetActivity extends BaseActivity {


	private Context mContext;
	private ImageView gender;
	private ImageView baby;
	private ImageButton saveQuit;

	private WheelView rotateView;

	private TuneWheel tuneWheel;
	private static Bitmap imageOriginal;
	private TextView weight_value, titleTxt;
	
	public String babyHeight, babyWeight;
	public boolean isboy;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_setting);
		mContext = this;
		getView();
		InitView();
		InitWeightView();
		InitHeightView();
		InitGenderView();
		SaveQuit();

	}
	
	private void getView() {
		gender = (ImageView) findViewById(R.id.gender_switch);
		baby = (ImageView) findViewById(R.id.baby);
		saveQuit = (ImageButton) findViewById(R.id.leftBtn);
		tuneWheel =  (TuneWheel) findViewById(R.id.tuneWheel);
		rotateView = (WheelView) findViewById(R.id.wheelView);
		weight_value = (TextView) findViewById(R.id.weight_value);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText(getString(R.string.baby_information));
	}


	private void InitView() {
		Intent data = getIntent();
		isboy = Boolean.valueOf(data.getStringExtra("babyGender"));
		if (!isboy) {
			gender.setImageResource(R.drawable.sex_girl);
			baby.setImageResource(R.drawable.girl);
		} else {
			gender.setImageResource(R.drawable.sex_boy);
			baby.setImageResource(R.drawable.boy);
		}
		babyHeight=data.getStringExtra("babyHeight");
		babyWeight=data.getStringExtra("babyWeight");
	}

	private void SaveQuit() {
		saveQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,BabyInfoActivity.class);
				intent.putExtra("height", babyHeight);
				intent.putExtra("weight", babyWeight);
				intent.putExtra("gender", isboy);
				sph.putString("babyHeight", babyHeight);
				sph.putString("babyWeight",babyWeight);
				sph.putBoolean("babyGender", isboy);
				
				setResult(RESULT_OK, intent);
				sph.putBoolean("hasSet",true);
				finish();
			}
		});

	}

	
	private void InitWeightView() {

		if (imageOriginal == null) {
			imageOriginal = BitmapFactory.decodeResource(getResources(),
					R.drawable.weight_rule).copy(Bitmap.Config.ARGB_8888, true);
		}

		rotateView.setOnWheelItemSelectedListener(new OnWheelItemSelectListener() {

					@Override
					public void onWheelItemSelected(int mode) {
						weight_value.setText(mode + "Kg");
						babyWeight=mode+"KG";
					}
				});
	}

	private void InitHeightView() {
		tuneWheel.setValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(float value) {
				babyHeight=value+"CM";
			}
		});

	}

	private void InitGenderView() {
		gender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isboy) {
					isboy = false;
					gender.setImageResource(R.drawable.sex_girl);
					baby.setImageResource(R.drawable.girl);
					sph.putBoolean("isboy", isboy);
				} else {
					isboy = true;
					gender.setImageResource(R.drawable.sex_boy);
					baby.setImageResource(R.drawable.boy);
					sph.putBoolean("isboy", isboy);
				}
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			SaveQuit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
