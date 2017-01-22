package com.haomee.liulian;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.PathConst;
import com.haomee.view.HintEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FeedbackActivity extends BaseActivity {

	private TextView feedback_toast;
	private ImageView tv_back;
	private TextView tv_submit;

	private String toast = "";
	private EditText et_cont, et_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_feedback);

		feedback_toast = (TextView) findViewById(R.id.feedback_toast);

		tv_back = (ImageView) findViewById(R.id.tv_back);

		tv_submit = (TextView) findViewById(R.id.tv_submit);

		et_cont = (EditText) findViewById(R.id.et_cont);

		et_contact = (EditText) findViewById(R.id.et_contact);
		et_contact.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		tv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		toast = "<font size=\"2\" color=\"#fe7777\">" + "@榴莲客户端#android客户端意见反馈#" + "</font><font size=\"2\" color=\"#000000\"><\br>" + "版本" + LiuLianApplication.appVersion_name + "</font>";

		feedback_toast.setText(Html.fromHtml(toast));

		tv_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String cont = et_cont.getText().toString().trim();
				StringBuffer sbf = new StringBuffer();
				sbf.append(toast).append(cont);
				if (!"".equals(cont)) {

					sub_feedfack(sbf.toString(), et_contact.getText().toString());

				} else {

					Toast.makeText(FeedbackActivity.this, "请输入您的宝贵意见", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	public void sub_feedfack(String msg, String contact) {

		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("cont", msg);
		re.put("contact", contact);
		re.put("phone_version", android.os.Build.MODEL + "");
		re.put("system_version", android.os.Build.VERSION.SDK + "");
		re.put("Luid", LiuLianApplication.current_user == null?"":LiuLianApplication.current_user.getUid());
		asyncHttp.post(PathConst.URL_FEEDFACK, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {

						Toast.makeText(FeedbackActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {

								FeedbackActivity.this.finish();

							}
						}, 1000);

					} else {

						Toast.makeText(FeedbackActivity.this, json.optString("msg"), Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
