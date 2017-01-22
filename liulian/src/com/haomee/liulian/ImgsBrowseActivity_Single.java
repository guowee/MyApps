package com.haomee.liulian;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.haomee.util.imageloader.ImageLoaderCharles;


public class ImgsBrowseActivity_Single extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acitivity_browser_single);

		ImageView iv_image = (ImageView) findViewById(R.id.iv_image);
		
		iv_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

        ImageLoaderCharles.getInstance(ImgsBrowseActivity_Single.this).addTask(getIntent().getStringExtra("url"),iv_image);

		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();

			}
		});

	}

}
