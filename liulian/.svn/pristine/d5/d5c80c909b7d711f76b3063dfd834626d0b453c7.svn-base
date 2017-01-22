package com.haomee.liulian;

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.entity.Users;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogAcitvity extends BaseActivity {
	private View top_view,bottom_view;
	private CircleImageView img_icon;
	private ImageView iv_dismiss;
	private TextView user_level;
	private TextView percent_1,percent_2,percent_3;
	private TextView message_notice;
	private TextView can_not_talk;
	private LinearLayout ll_start_talk;
	private TextView talk;
	private Intent intent;
	private int flag=0;
	private int per=0;
	private Users user=null;
	private Activity activity_context;
	private Intent intent_breadcast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitvity_dialog	);
		intent=getIntent();
		flag=intent.getIntExtra("flag", 0);
		per=intent.getIntExtra("per", 0);
		user=(Users) intent.getSerializableExtra("user");
		intent_breadcast = new Intent("MyReceiver_Test");
		activity_context=this;
		initView ();
	}
	private void initView() {
		// TODO Auto-generated method stub
		top_view=findViewById(R.id.top_view);
		bottom_view=findViewById(R.id.bottom_view);
		img_icon=(CircleImageView) findViewById(R.id.img_icon);
		iv_dismiss=(ImageView) findViewById(R.id.bt_back);
		user_level=(TextView) findViewById(R.id.user_level);
		percent_1=(TextView) findViewById(R.id.percent_1);
		percent_2=(TextView) findViewById(R.id.percent_2);
		percent_3=(TextView) findViewById(R.id.percent_3);
		message_notice=(TextView) findViewById(R.id.message_notice);
		can_not_talk=(TextView) findViewById(R.id.can_not_talk);
		ll_start_talk=(LinearLayout) findViewById(R.id.ll_start_talk);
		talk=(TextView) findViewById(R.id.talk);
		
		top_view.setOnClickListener(clickListener);
		bottom_view.setOnClickListener(clickListener);
		iv_dismiss.setOnClickListener(clickListener);
		ll_start_talk.setOnClickListener(clickListener);
		
		if(user!=null){
			if(flag==0){//匹配失败
				percent_1.setText("兴趣匹配值达");
				percent_2.setText(per+"%");
				percent_3.setText("未达标");
				message_notice.setText("请关注TA, 升级到LV."+user.getUser_level());
				ll_start_talk.setVisibility(View.GONE);
				can_not_talk.setVisibility(View.VISIBLE);
			}else {//可以聊天
				percent_1.setText("达到");
				percent_2.setText(per+"%");
				percent_3.setVisibility(View.GONE);
				message_notice.setText("真是赤裸裸的灵魂伴侣");
				talk.setText("开始对话");
				ll_start_talk.setVisibility(View.VISIBLE);
				can_not_talk.setVisibility(View.GONE);
			}
            ImageLoaderCharles.getInstance(DialogAcitvity.this).addTask(user.getImage(),img_icon);
			user_level.setText("Level "+user.getUser_level());
		}
	}

	/**
	 * 处理点击事件
	 */
	OnClickListener clickListener=new OnClickListener() {

		

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.top_view:
				finish();
				break;
			case R.id.bottom_view:
				finish();
				break;
			case R.id.bt_back:
				if("开始对话".equals(talk.getText().toString().trim())){
					intent_breadcast.putExtra("is_can_talk", true);
					activity_context.sendBroadcast(intent_breadcast);
				}
				finish();
				break;
			case R.id.ll_start_talk://进入聊天界面
				if(user!=null){
					Intent intent=new Intent();
					intent.setClass(activity_context, ChatActivity.class);
					intent.putExtra("uId", user.getUid());// 聊天对象的uid
					intent.putExtra("userId", user.getHx_username());// 聊天对象的环信ID
					intent.putExtra("nickname", user.getName());// 聊天对象的昵称
					startActivity(intent);
					StatService.onEvent(activity_context, "ta_homepage_chat", "ta的主页聊天点击次数", 1);
					intent_breadcast.putExtra("is_can_talk", true);
					activity_context.sendBroadcast(intent_breadcast);
					finish();
				}
				break;
			}

		}
	};
}
