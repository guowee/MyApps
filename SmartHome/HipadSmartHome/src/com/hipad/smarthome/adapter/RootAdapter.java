package com.hipad.smarthome.adapter;

import com.hipad.smarthome.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

public class RootAdapter extends AbstractWheelTextAdapter {

	public String typeStr[] = null;
	
	public RootAdapter(Context context,int resID,String[] items) {
		super(context, resID, NO_RESOURCE);
		typeStr = items;
		setItemTextResource(R.id.text_name);
	}
	
	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		// ImageView img = (ImageView) view.findViewById(R.id.flag);
		// img.setImageResource(flags[index]);
		return view;
	}

	@Override
	public int getItemsCount() {
		return typeStr.length;
	}

	@Override
	public CharSequence getItemText(int index) {
		return typeStr[index];
	}


}
