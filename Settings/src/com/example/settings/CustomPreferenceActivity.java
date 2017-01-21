package com.example.settings;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomPreferenceActivity extends PreferenceActivity {
    List<Header> mCopyHeaders;

    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private static class HeaderViewHolder {
            ImageView icon;
            TextView title;
            TextView summary;
            ImageView arrow;
        }

        private LayoutInflater mInflater;

        public HeaderAdapter(Context context, List<Header> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            View view;

            if (convertView == null) {

                view = mInflater.inflate(R.layout.preference_header_item, parent, false);

                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.icon);
                holder.title = (TextView) view.findViewById(android.R.id.title);
                holder.summary = (TextView) view.findViewById(android.R.id.summary);
                holder.arrow = (ImageView) view.findViewById(R.id.arrow);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            // All view fields must be updated every time, because the view may be recycled
            Header header = getItem(position);
            holder.icon.setImageResource(header.iconRes);
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }

            return view;
        }
    }

    @Override
    public void loadHeadersFromResource(int resid, List<Header> target) {
        super.loadHeadersFromResource(resid, target);
        mCopyHeaders = target;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mCopyHeaders != null && mCopyHeaders.size() > 0) {
            setListAdapter(new HeaderAdapter(this, mCopyHeaders));
        }
    }
}
