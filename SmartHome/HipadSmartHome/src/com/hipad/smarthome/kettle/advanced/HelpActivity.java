package com.hipad.smarthome.kettle.advanced;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.R;

/**
 * °ïÖú
 * 
 * @author guowei
 *
 */
public class HelpActivity extends BaseActivity implements IFunction,
		OnClickListener {
	public static final String MYACTION = "HelpActivity_action";

	private WebView helpView;
	private ImageButton backBtn;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help_layout);

		initViews();
	}

	private void initViews() {

		String titleStr = getIntent().getStringExtra("title");

		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(this);
		title = (TextView) findViewById(R.id.titleTxt);
		title.setText(titleStr);
		helpView = (WebView) findViewById(R.id.help_view);
		helpView.loadUrl("file:///android_asset/help.html");
	}

	@Override
	public String getName() {
		return "°ïÖú";
	}

	@Override
	public Intent execute(Context context) {

		Intent intent = new Intent();

		intent.setAction(MYACTION);

		return intent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean isForResult() {
		return false;
	}
}
