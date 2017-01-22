package com.hipad.tracker;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import com.hipad.tracker.dialog.SetNameDialog;
import com.hipad.tracker.dialog.TimeSetPopWindow;
import com.hipad.tracker.dialog.SetNameDialog.OnSetNameDialogListener;
import com.hipad.tracker.dialog.TimeSetPopWindow.TimeChoseListener;
import com.hipad.tracker.entity.BabyInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author guowei
 *
 */
public class BabyInfoActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private ImageButton leftBtn;
	private TextView titleText;

	private ImageView headofbaby;
	private TextView nameofbaby;
	private TextView genderofbaby;
	private TextView trackernum;
	private TextView birthofbaby;
	private TextView heightofbaby;
	private TextView weightofbaby;
	private RelativeLayout nameView, genderView, birthView, heightView,weightView;
	public String babyname, babygender, babybirth, babyheight, babyweight;

	
	private SetNameDialog mDialog;
	public Calendar calendar = Calendar.getInstance();
	public final static int REQUEST_CODE = 1;
	public static BabyInfo babyInfo=new BabyInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_info_layout);
		mContext = this;
		getViews();
	}
	
	@Override
	protected void onStart() {
		initUI();
		super.onStart();
	}
	private void initUI(){
		String name=sph.getString("babyName");
	    boolean isBoy=sph.getBoolean("babyGender");
	    String birthday=sph.getString("babyBirthday");
	    String height=sph.getString("babyHeight");
	    String weight=sph.getString("babyWeight");
	    nameofbaby.setText(name);
	    if(isBoy){
	    	headofbaby.setBackground(getResources().getDrawable(R.drawable.head_boy));
	    	genderofbaby.setText("boy");
	    }else{
	    	headofbaby.setBackground(getResources().getDrawable(R.drawable.head_girl));
	    	genderofbaby.setText("girl");
	    }
	    trackernum.setText(sph.getString("info_devSim"));
		birthofbaby.setText(birthday);
		heightofbaby.setText(height);
        weightofbaby.setText(weight);   
	}
	
	private void getViews() {
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		titleText = (TextView) findViewById(R.id.titleTxt);
		titleText.setText(getString(R.string.baby_information));

		headofbaby = (ImageView) findViewById(R.id.baby_head_img);
		nameofbaby = (TextView) findViewById(R.id.nameofbaby);
		genderofbaby = (TextView) findViewById(R.id.genderofbaby);
		birthofbaby = (TextView) findViewById(R.id.birthofbaby);
		trackernum = (TextView) findViewById(R.id.numoftracker);
		heightofbaby = (TextView) findViewById(R.id.heightofbaby);
		weightofbaby = (TextView) findViewById(R.id.weightofbaby);

		nameView = (RelativeLayout) findViewById(R.id.name_view);
		genderView = (RelativeLayout) findViewById(R.id.gender_view);
		birthView = (RelativeLayout) findViewById(R.id.birth_view);
		heightView = (RelativeLayout) findViewById(R.id.Height_view);
		weightView = (RelativeLayout) findViewById(R.id.Weight_view);

		setClickListener();
	}

	private void setClickListener() {
		leftBtn.setOnClickListener(this);
		nameView.setOnClickListener(this);
		genderView.setOnClickListener(this);
		birthView.setOnClickListener(this);
		heightView.setOnClickListener(this);
		weightView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;

		case R.id.name_view:

			mDialog = new SetNameDialog(mContext, R.style.MyDialog,	new OnSetNameDialogListener() {
						@Override
						public void OnSetNameClick(String name) {

							if (name == null || name.equals("")) {
								name = getString(R.string.baby_name_default);
							}
							sph.putString("babyName", name);
							nameofbaby.setText(name);
						}
					});

			mDialog.show();

			break;
		case R.id.birth_view:
			TimeSetPopWindow popWindow=new TimeSetPopWindow(mContext);
			popWindow.setTimeChoseListener(new TimeChoseListener() {
				
				@Override
				public void onTimeChoose(int year, int month, int day) {
					StringBuffer sb = new StringBuffer(); 
                    sb.append(String.format("%d-%02d-%02d", year, month,day)); 
                    birthofbaby.setText(sb.toString());
                    sph.putString("babyBirthday",sb.toString() );
				}

			});
			popWindow.showPopupWindow();
			break;
		case R.id.gender_view:
		case R.id.Height_view:
		case R.id.Weight_view:
			Intent intent =new Intent(mContext,BabyInformSetActivity.class);
			intent.putExtra("babyGender", genderofbaby.getText().toString().trim());
			intent.putExtra("babyHeight", heightofbaby.getText().toString().trim());
			intent.putExtra("babyWeight", weightofbaby.getText().toString().trim());
			startActivityForResult(intent,REQUEST_CODE);
			break;
		default:
			break;
		}

	}
	private void updateUI(BabyInfo info){
		headofbaby.setBackground(getResources().getDrawable(info.getIcon()));
		nameofbaby.setText(info.getName());
		genderofbaby.setText(info.getGender());
		birthofbaby.setText(info.getBirthday());
		heightofbaby.setText(info.getHeight());
        weightofbaby.setText(info.getWeight());      		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
			boolean isboy=data.getBooleanExtra("gender", false);
			if(isboy){
				genderofbaby.setText("boy");
				headofbaby.setBackground(getResources().getDrawable(R.drawable.head_boy));
			}else{
                genderofbaby.setText("girl");	
                headofbaby.setBackground(getResources().getDrawable(R.drawable.head_girl));
			}
			heightofbaby.setText(data.getStringExtra("height"));
			weightofbaby.setText(data.getStringExtra("weight"));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onStop() {
		//saveBabyInfo();
		super.onStop();
	}
	
	public void saveBabyInfo() {
        BabyInfo baby=new BabyInfo();
        String gender=genderofbaby.getText().toString();
        baby.setGender(gender);
        if("girl".equals(gender)){
        	baby.setIcon(R.drawable.head_girl);
        }else{
        	baby.setIcon(R.drawable.head_boy);
        }
        baby.setName(nameofbaby.getText().toString());
        baby.setBirthday(birthofbaby.getText().toString());
        baby.setHeight(heightofbaby.getText().toString());
        baby.setWeight(weightofbaby.getText().toString());
        
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(getCacheDir()+ File.separator+MyApplication.user.getMobile() + "_babyInfo");
			oos = new ObjectOutputStream(fos);
			if (null != baby)
				oos.writeObject(baby);
			oos.flush();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != oos)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void restoreBabyInfo(){
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(getCacheDir()+ File.separator + MyApplication.user.getMobile() + "_babyInfo");
			ois = new ObjectInputStream(fis);
			babyInfo = (BabyInfo) ois.readObject();
			updateUI(babyInfo);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != ois)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
