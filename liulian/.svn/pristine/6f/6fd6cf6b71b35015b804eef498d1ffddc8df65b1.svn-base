package com.haomee.liulian;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SendTextContent extends BaseActivity {

	private TextView publish;
	private EditText content;
	private ImageView user_icon;
	private TextView user_name;
	private TextView content_title;
	private String title;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_content_text);
		
		loadingDialog = new LoadingDialog(this);
		

		publish = (TextView) findViewById(R.id.publish);
		content = (EditText) findViewById(R.id.content);

		content_title = (TextView) findViewById(R.id.content_title);
		user_icon = (ImageView) findViewById(R.id.user_icon);
		user_name = (TextView) findViewById(R.id.name);

		title = getIntent().getStringExtra("title");
//		content_title.setText();
		content.setHint("#"+title+"#");
		content.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);


        ImageLoaderCharles.getInstance(SendTextContent.this).addTask(LiuLianApplication.current_user.getImage(),user_icon);
		user_name.setText(LiuLianApplication.current_user.getName());
		user_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);

		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		publish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String temp = content.getText().toString();
				if (!temp.equals("")) {
					publish.setClickable(false);
					StringBuffer sbf =  new StringBuffer();
					sbf.append(content.getText().toString()).append("\t"+"#"+title+"#");
					loadingDialog.show();
					addContent(getIntent().getStringExtra("topic_id"), sbf.toString());
				} else {
					MyToast.makeText(SendTextContent.this, "请输入文字", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void addContent(String topic_id, String text_content) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		re.put("topic_id", topic_id);
		re.put("text_content", text_content);
		asyncHttp.get(PathConst.URL_CONTENT_ADD, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("地址：", json.toString());
					if (1 == json.optInt("flag")) {
						if (json.has("egg")) {
							JSONArray json_arr = json.getJSONArray("egg");
							JSONObject egg_obj = json_arr.getJSONObject(0);
							Intent intent_send = new Intent();
							intent_send.setClass(SendTextContent.this, ShareMedalActivity.class);
							ShareContent share = new ShareContent();
							share.setId(egg_obj.getString("id"));
							share.setTitle(egg_obj.getString("name"));
							share.setSummary(egg_obj.getString("desc"));
							share.setImg_url(egg_obj.getString("icon"));
							share.setRedirect_url(CommonConst.GOV_URL);
							intent_send.putExtra("share", share);
							SendTextContent.this.startActivity(intent_send);
						}
						MyToast.makeText(SendTextContent.this, "发布成功", 1).show();
						publish.setClickable(true);
						finish();
					} else {
						publish.setClickable(true);
						MyToast.makeText(SendTextContent.this, "发布失败", 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResultValue();
		super.onBackPressed();
	}

	// setResult在onDestory中不生效
	private void setResultValue() {
		Intent intent = new Intent(SendTextContent.this, ContentList.class);
		setResult(ContentList.RESUlT_SEND_SUCCESS, intent);

	}
}
