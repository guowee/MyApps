package com.example.fragment;

import com.example.teabaike.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FunctionTeaFragment extends Fragment {
	/** 搜索文本框 */
	private EditText editText_funtea_searchEdit;
	/** 搜索按钮 */
	private ImageView imageView_funtea_searchBtn;
	/** 热门搜索：茶 */
	private TextView textView_funtea_serachtea;
	/** 我的收藏 */
	private TextView textView_funtea_mycollect;
	/** 查看访问记录 */
	private TextView textView_funtea_selectlog;
	/** 版权信息 */
	private TextView textView_funtea_copyright;
	/** 意见反馈 */
	private TextView textView_funtea_feedback;
	/** 二维码 */
	private TextView textView_funtea_qrcode;
	/** 地图 */
	private TextView textView_funtea_map;

	MyClickListener mListener;
	/** 接口回调类型，与Activity交互用 */
	MyButtonClickListener buttonListener;

	/** 编辑框动画对象 */
	Animation shake;

	public FunctionTeaFragment() {

	}

	public FunctionTeaFragment(Context context) {

		buttonListener = (MyButtonClickListener) context;

		shake = AnimationUtils.loadAnimation(context, R.anim.shake);// 加载动画资源文件
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mListener = new MyClickListener();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_fun_tea, null);
		editText_funtea_searchEdit = (EditText) view
				.findViewById(R.id.editText_funtea_searchEdit);
		imageView_funtea_searchBtn = (ImageView) view
				.findViewById(R.id.imageView_funtea_searchBtn);
		textView_funtea_serachtea = (TextView) view
				.findViewById(R.id.textView_funtea_serachtea);
		textView_funtea_mycollect = (TextView) view
				.findViewById(R.id.textView_funtea_mycollect);
		textView_funtea_selectlog = (TextView) view
				.findViewById(R.id.textView_funtea_selectlog);
		textView_funtea_copyright = (TextView) view
				.findViewById(R.id.textView_funtea_copyright);
		textView_funtea_feedback = (TextView) view
				.findViewById(R.id.textView_funtea_feedback);
		textView_funtea_qrcode = (TextView) view
				.findViewById(R.id.textView_funtea_qrcode);

		textView_funtea_map = (TextView) view
				.findViewById(R.id.textView_funtea_map);

		imageView_funtea_searchBtn.setOnClickListener(mListener);
		textView_funtea_serachtea.setOnClickListener(mListener);
		textView_funtea_mycollect.setOnClickListener(mListener);
		textView_funtea_selectlog.setOnClickListener(mListener);
		textView_funtea_copyright.setOnClickListener(mListener);
		textView_funtea_feedback.setOnClickListener(mListener);

		textView_funtea_qrcode.setOnClickListener(mListener);

		textView_funtea_map.setOnClickListener(mListener);

		return view;

	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.imageView_funtea_searchBtn:

				String searchStr = editText_funtea_searchEdit.getText()
						.toString();

				if (!"".equals(searchStr)) {

					buttonListener.onMyButtonClick(7, searchStr);

				} else {

					editText_funtea_searchEdit.startAnimation(shake); // 给组件播放动画效果
				}

				break;

			case R.id.textView_funtea_serachtea:
				buttonListener.onMyButtonClick(7, "茶");

				break;
			case R.id.textView_funtea_mycollect:

				buttonListener.onMyButtonClick(1, null);
				break;
			case R.id.textView_funtea_selectlog:

				buttonListener.onMyButtonClick(2, null);
				break;
			case R.id.textView_funtea_copyright:

				buttonListener.onMyButtonClick(3, null);
				break;
			case R.id.textView_funtea_feedback:

				buttonListener.onMyButtonClick(4, null);
				break;

			case R.id.textView_funtea_qrcode:
				
				buttonListener.onMyButtonClick(5, null);
				break;

			case R.id.textView_funtea_map:
				buttonListener.onMyButtonClick(6, null);
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 用在与activity交互时，被回调的接口
	 * 
	 */
	public interface MyButtonClickListener {

		/**
		 * 查搜索首页面单击监听
		 * 
		 * @param titleTag
		 *            title标记
		 * @param text
		 *            如果是搜索页面，那么把传值带过去，否则写null就行
		 */

		public void onMyButtonClick(int titleTag, String text);
	}

}
