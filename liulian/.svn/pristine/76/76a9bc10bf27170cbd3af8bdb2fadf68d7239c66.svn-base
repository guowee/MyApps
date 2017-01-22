package com.haomee.liulian;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.haomee.adapter.ReportAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Report;
import com.haomee.view.HintEditText;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ReportActivity extends BaseActivity {

	private TextView report_title, report_content, report_ok;
	private EditText report_editText;
	private GridView report_gridView;
	private List<Report> list;
	private Report report;
	private ReportAdapter reportItemAdapter;
	private String content_id;
	private String content_;
	private String object_type;
	private String[] repost_list_name = {

	"垃圾营销", "淫秽色情", "不实信息", "敏感信息", "抄袭内容", "骚扰我", "其他", };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_report);

		report_title = (TextView) findViewById(R.id.report_title);
		report_content = (TextView) findViewById(R.id.report_content);
		report_ok = (TextView) findViewById(R.id.report_ok);
		report_editText = (EditText) findViewById(R.id.report_editText);
		report_editText.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		report_gridView = (GridView) findViewById(R.id.report_gridView);

		object_type = getIntent().getStringExtra("report_type");
		String title = getIntent().getStringExtra("title");
		content_id = getIntent().getStringExtra("content_id");
		String content = getIntent().getStringExtra("content");

		StringBuffer sbftitle = new StringBuffer();
		sbftitle.append("@").append(title);
		// 内容
		if (object_type.equals(CommonConst.REPOST_TYPE_CONTENT)) {

			report_title.setText(Html.fromHtml("<font size=\"2\" color=\"#555555\">举报</font><font size=\"2\" color=\"#fe7777\">" + sbftitle.toString() + "</font><font size=\"2\" color=\"#555555\">的内容</font>"));

			if (!content.equals("")) {

				report_content.setText(Html.fromHtml("<font size=\"2\" color=\"#fe7777\">" + sbftitle.toString() + "</font><font size=\"2\" color=\"#555555\">" + "  " + content + "</font>"));

			} else {

				report_content.setVisibility(View.GONE);
			}

			// 话题
		} else if (object_type.equals(CommonConst.REPOST_TYPE_DISCOVER)) {

			report_title.setText(Html.fromHtml("<font size=\"2\" color=\"#555555\">举报</font><font size=\"2\" color=\"#fe7777\">" + title + "</font><font size=\"2\" color=\"#555555\">的话题</font>"));

			if (!content.equals("")) {

				report_content.setText(Html.fromHtml("<font size=\"2\" color=\"#fe7777\">" + sbftitle.toString() + "</font><font size=\"2\" color=\"#555555\">" + "  " + content + "</font>"));

			} else {

				report_content.setVisibility(View.GONE);
			}

		}

		list = new ArrayList<Report>();

		for (int i = 0; i < repost_list_name.length; i++) {

			report = new Report();

			report.setId(i + "");

			report.setName(repost_list_name[i]);

			report.setIs_select(false);

			list.add(report);

		}
		reportItemAdapter = new ReportAdapter(this, list);
		report_gridView.setAdapter(reportItemAdapter);

		report_gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				reportItemAdapter.changeState(position);

				if (position != 0 && position % 2 == 0) {

					report_editText.setVisibility(View.VISIBLE);

				} else {

					report_editText.setVisibility(View.GONE);
				}

				content_ = list.get(position).getName();

			}
		});

		report_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				report(content_id, content_, object_type);
			}
		});

		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

	}

	public void report(String content, String type, String object_type) {

		Log.e("object_type", object_type + "");

		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("object_id", content_id);
		re.put("object_type", object_type);
		re.put("content", content);
		re.put("type", type);
		// Log.e("地址：", PathConst.URL_REPORT + "&login_uid=" +
		// LiuLianApplication.current_user.getUid() + "&to_hx_username=" +
		// LiuLianApplication.current_user.getHx_username());
		asyncHttp.get(PathConst.URL_REPORT, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("数据：", json.toString());
					if (1 == json.optInt("flag")) {

						MyToast.makeText(ReportActivity.this, "举报成功", 1).show();

						ReportActivity.this.finish();

					} else {
						MyToast.makeText(ReportActivity.this, "举报失败", 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
