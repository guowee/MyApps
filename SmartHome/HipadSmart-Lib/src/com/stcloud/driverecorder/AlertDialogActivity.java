package com.stcloud.driverecorder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogActivity extends Activity {

	public static int MSG_TF_NOT_EXIST = 1;
	public static int MSG_INVALID_TF_SIZE = 2;
	
	//soundPool
	private SoundPool soundPool=null;
	private int musicID_01;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        
		///SoundPool //20150810 sam add for mark 206 dialog
		soundPool= new SoundPool(10,AudioManager.STREAM_MUSIC,5);
		musicID_01 =soundPool.load(getApplicationContext(),R.raw.bee,1);

        setContentView(R.layout.dialog_activity);
        
        int type = getIntent().getIntExtra("type", 1);
        TextView tv = (TextView)findViewById(R.id.tvMsg);
        if (type == MSG_TF_NOT_EXIST) {
        	tv.setText(getString(R.string.tf_not_exist));
        	
        //20150810 sam add for mark 206 dialog
        	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
				soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
				}
				}, 20);
        //20150810 sam add for mark 206 dialog
        	
        } else if (type == MSG_INVALID_TF_SIZE) {
        	tv.setText(getString(R.string.invalid_tf_size));
        } else {
        	
        }
        
        getWindow().setTitle("Error");

        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                android.R.drawable.ic_dialog_alert);
        
        Button button = (Button)findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    }	
}
