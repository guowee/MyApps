package com.example.cricleprogress;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private EditText editNum;
	private ImageView iconView;
	private Button startBtn;

	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0x123) {

				if (msg.arg1 <= 100 && msg.arg1 >= 0) {

					CanvasUtils.getProgressBitmap(iconView, getResources(),
							R.drawable.doughnutchart_tds, msg.arg1);
				}

			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

	}

	private void init() {
		editNum = (EditText) findViewById(R.id.edit_Num);
		iconView = (ImageView) findViewById(R.id.image);
		startBtn = (Button) findViewById(R.id.start);
		startBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.start:

			String temp = editNum.getText().toString();

			final Integer number = Integer.valueOf(temp);
		
			new Timer().schedule(new TimerTask() {
				
				int progress=0;
				@Override
				public void run() {

					Message msg = new Message();

					msg.what = 0x123;

					msg.arg1 = progress++;
					
					if(progress>number){
						
						this.cancel();
					}

					handler.sendMessage(msg);

				}
			}, 10, 50);

			/*
			 * new Thread() {
			 * 
			 * @Override public void run() { // TODO
			 * 子线程中通过handler发送消息给handler接收，由handler去更新TextView的值 try { Message
			 * msg = new Message();
			 * 
			 * msg.what = 0x123;
			 * 
			 * msg.arg1 = number;
			 * 
			 * handler.sendMessage(msg);
			 * 
			 * } catch (Exception e) { e.printStackTrace(); } } }.start();
			 */

			break;

		default:
			break;
		}
	}

}
