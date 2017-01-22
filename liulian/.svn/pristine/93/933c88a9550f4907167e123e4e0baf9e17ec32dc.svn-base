package com.haomee.liulian;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.haomee.adapter.BlackListAdapter;

//搜索
public class BlackListActivity extends BaseActivity {
	private ListView list_black;
	private List<String> black_list;
	private BlackListAdapter blackAdapter;
	private View layout_blank_tip;
	private TextView tip1, tip2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_black_list);
		StatService.onEvent(BlackListActivity.this, "setting_black_list", "设置页黑名单点击次数", 1);
		list_black = (ListView) findViewById(R.id.list_black);
		initBlankTip();
		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		black_list = EMContactManager.getInstance().getBlackListUsernames();
		if (black_list == null) {
			black_list = new ArrayList<String>();
		}
		if (black_list.size() == 0) {
			showBlankTip("你还没有拉黑过一个LIer哦！","去招呼页向左滑动ta试试哦！");
			list_black.setVisibility(View.GONE);
		} else {
			blackAdapter = new BlackListAdapter(this, black_list);
			list_black.setAdapter(blackAdapter);
		}
	}

	private void initBlankTip() {
		layout_blank_tip = findViewById(R.id.layout_blank_tip);
		tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
		tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
		hideBlankTip();
	}

	// 空白页提示
	public void showBlankTip(String t1, String t2) {
		layout_blank_tip.setVisibility(View.VISIBLE);

		if (LiuLianApplication.height_fragment_liulian > 0) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout_blank_tip.getLayoutParams();
			params.height = LiuLianApplication.height_fragment_liulian;
			layout_blank_tip.setLayoutParams(params);
		}

		tip1.setText(t1);
		tip2.setText(t2);
	}

	public void hideBlankTip() {
		layout_blank_tip.setVisibility(View.GONE);
	}
}
