package com.haomee.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.entity.SystemMessage;
import com.haomee.entity.Topic;
import com.haomee.liulian.FeedbackActivity;
import com.haomee.liulian.R;
import com.haomee.liulian.TopicDetailActivity;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.liulian.WebPageActivity;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class SystemMessageAdapter extends BaseAdapter {

	private Context context;
	private List<SystemMessage> list_mesage;
	private LayoutInflater inflater;

	public SystemMessageAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<SystemMessage> list) {
		this.list_mesage = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_mesage == null ? 0 : list_mesage.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_mesage.get(position);

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder holder;

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_message, null);
			holder.item_content1 = (TextView) convertView.findViewById(R.id.item_message);
			holder.item_image = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String[] strs = null;
		StringBuilder actionText = new StringBuilder();

		if (list_mesage.get(position).getS_meg_type().equals("2")) {
			strs = list_mesage.get(position).getS_cont().split("[{}]");
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].equals("1")) {
					actionText.append(strs[0]).append("<a style=\"text-decoration:none;\" href='username'>" + "@" + list_mesage.get(position).getUsers().getName() + "</a> ").append(strs[2]);
				}
			}
			holder.item_content1.setText(Html.fromHtml(actionText.toString()));
			holder.item_content1.setMovementMethod(LinkMovementMethod.getInstance());
			CharSequence text = holder.item_content1.getText();
			int ends = text.length();
			Spannable spannable = (Spannable) holder.item_content1.getText();
			URLSpan[] urlspan = spannable.getSpans(0, ends, URLSpan.class);
			SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(text);
			stylesBuilder.clearSpans(); // should clear old spans
			for (int index_url = 0; index_url < urlspan.length; index_url++) {
				TextViewURLSpan myURLSpan = new TextViewURLSpan(0, urlspan[index_url].getURL(), list_mesage.get(position).getUsers().getUid());
				stylesBuilder.setSpan(myURLSpan, spannable.getSpanStart(urlspan[index_url]), spannable.getSpanEnd(urlspan[index_url]), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			}

			holder.item_content1.setText(stylesBuilder);

		} else if (list_mesage.get(position).getS_meg_type().equals("3")) {

			strs = list_mesage.get(position).getS_cont().split("[{}]");
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].equals("1")) {
					actionText.append(strs[0]).append(" <a style=\"text-decoration:none;\" href='topic'>" + list_mesage.get(position).getTopic().getTitle() + " </a> ").append(strs[2]);
				}
			}
			holder.item_content1.setText(Html.fromHtml(actionText.toString()));
			holder.item_content1.setMovementMethod(LinkMovementMethod.getInstance());
			CharSequence text = holder.item_content1.getText();
			int ends = text.length();
			Spannable spannable = (Spannable) holder.item_content1.getText();
			URLSpan[] urlspan = spannable.getSpans(0, ends, URLSpan.class);
			SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(text);
			stylesBuilder.clearSpans(); // should clear old spans
			for (int index_url = 0; index_url < urlspan.length; index_url++) {
				TextViewURLSpan myURLSpan = new TextViewURLSpan(0, urlspan[index_url].getURL(), list_mesage.get(position).getTopic());
				stylesBuilder.setSpan(myURLSpan, spannable.getSpanStart(urlspan[index_url]), spannable.getSpanEnd(urlspan[index_url]), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			}
			holder.item_content1.setText(stylesBuilder);
		} else if (list_mesage.get(position).getS_meg_type().equals("4")) {
			strs = list_mesage.get(position).getS_cont().split("[{}]");
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].equals("1")) {
					actionText.append(strs[0]).append(" <a style=\"text-decoration:none;\" href='feed'>" + list_mesage.get(position).getFeed_str() + " </a>").append(strs[2]);
				}
			}
			holder.item_content1.setText(Html.fromHtml(actionText.toString()));
			holder.item_content1.setMovementMethod(LinkMovementMethod.getInstance());
			CharSequence text = holder.item_content1.getText();
			int ends = text.length();
			Spannable spannable = (Spannable) holder.item_content1.getText();
			URLSpan[] urlspan = spannable.getSpans(0, ends, URLSpan.class);
			SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(text);
			stylesBuilder.clearSpans(); // should clear old spans
			for (int index_url = 0; index_url < urlspan.length; index_url++) {
				TextViewURLSpan myURLSpan = new TextViewURLSpan(0, urlspan[index_url].getURL());
				stylesBuilder.setSpan(myURLSpan, spannable.getSpanStart(urlspan[index_url]), spannable.getSpanEnd(urlspan[index_url]), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			holder.item_content1.setText(stylesBuilder);
		} else if (list_mesage.get(position).getS_meg_type().equals("5")) {
			actionText.append(list_mesage.get(position).getS_cont());
			holder.item_content1.setText(actionText.toString());
			holder.item_content1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(context, WebPageActivity.class);
					intent.putExtra("url", list_mesage.get(position).getH5_url());
					context.startActivity(intent);
				}
			});
		} else {
			actionText.append(list_mesage.get(position).getS_cont());
			holder.item_content1.setText(actionText.toString());
		}
		ImageLoaderCharles.getInstance(context).addTask(list_mesage.get(position).getIcon(), holder.item_image);
		holder.time.setText(list_mesage.get(position).getCreate_time() + "");
		return convertView;
	}

	private class TextViewURLSpan extends ClickableSpan {
		private String clickString;
		private String uid;
		private Topic topic;
		private int index_url;

		public TextViewURLSpan(int index_url, String clickString) {
			this.clickString = clickString;
			this.index_url = index_url;
		}

		public TextViewURLSpan(int index_url, String clickString, String uid) {
			this.clickString = clickString;
			this.uid = uid;
			this.index_url = index_url;
		}

		public TextViewURLSpan(int index_url, String clickString, Topic topic) {
			this.clickString = clickString;
			this.topic = topic;
			this.index_url = index_url;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			switch (index_url) {
			case 0:
				ds.setColor(context.getResources().getColor(R.color.sys_info_1));
				break;
			case 1:
				ds.setColor(context.getResources().getColor(R.color.sys_info_2));
				break;
			case 2:
				ds.setColor(context.getResources().getColor(R.color.sys_info_3));
				break;
			}
			ds.setUnderlineText(false); // 去掉下划线
		}

		@Override
		public void onClick(View widget) {
			Intent intent = new Intent();
			if (clickString.equals("username")) {
				intent.setClass(context, UserInfoDetail.class);
				intent.putExtra("uid", uid);
				context.startActivity(intent);
			} else if (clickString.equals("topic")) {
				intent.setClass(context, TopicsDetailActivity.class);
				intent.putExtra("topic_id", topic.getId());
				context.startActivity(intent);
			} else if (clickString.equals("feed")) {
				intent.setClass(context, FeedbackActivity.class);
				context.startActivity(intent);
			}
		}
	}

	class ViewHolder {
		private ImageView item_image;
		private TextView item_content1;
		private TextView time;
	}
}
