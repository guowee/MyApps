package com.haomee.liulian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 帮助提示浮层
 */
public class HelpTipActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		
		final String from = intent.getStringExtra("from");
		
		if("tip_daka".equals(from)){
			setContentView(R.layout.help_tip_daka);
		}else if("tip_hi".equals(from)){
			setContentView(R.layout.help_tip_hi);
		}else if("tip_share".equals(from)){
			setContentView(R.layout.help_tip_share);
		}else if("tip_add_topic".equals(from)){
			setContentView(R.layout.help_tip_add_topic);
		}else if("tip_radar".equals(from)){
			setContentView(R.layout.help_tip_radar);
		}else if("tip_users".equals(from)){
			setContentView(R.layout.help_tip_users);
		}
		

		findViewById(R.id.tip_body).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}
	

}
