package com.example.fragment;

import com.example.helper.QRCodeHelper;
import com.example.teabaike.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class QRCodeFragment extends Fragment {

	private ImageView qrcode_icon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_qrcode, null);

		qrcode_icon = (ImageView) view.findViewById(R.id.qrcode_icon);

		String url = "http://sns.maimaicha.com/";

		QRCodeHelper.createQRCodeImage(url, qrcode_icon);

		return view;
	}

}
