package com.hipad.smarthome;

import com.hipad.smarthome.utils.MTextView;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 
 * @author guowei
 *
 */
public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView title, promptView;
    private MTextView profileView;
	private ImageButton backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about_layout);

		getView();
		setProfile();
		setHyperlink();

	}

	private void getView() {
		title = (TextView) findViewById(R.id.titleTxt);
		title.setText("关于我们");
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(this);

		profileView = (MTextView) findViewById(R.id.profile_company);
		promptView = (TextView) findViewById(R.id.tv_url);

	}

	
	
	
	private void setProfile(){
		String profile=getString(R.string.about_company);
		SpannableString ss = new SpannableString(profile);
		profileView.setMText(ss);
		profileView.setTextSize(18);
		profileView.setTextColor(Color.BLACK);
		
		profileView.invalidate();
		
	}
	
	
	private void setHyperlink() {

		SpannableString sp = new SpannableString("公司网站：www.donlim.com");
		sp.setSpan(new URLSpan("http://www.donlim.com"), 0, sp.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		sp.setSpan(new ForegroundColorSpan(Color.rgb(120, 152, 194)), 0,
				sp.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		promptView.setText(sp);
		promptView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		promptView.setMovementMethod(LinkMovementMethod.getInstance());

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

}
