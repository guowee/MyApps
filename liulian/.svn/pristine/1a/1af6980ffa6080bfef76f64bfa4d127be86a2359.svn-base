package com.haomee.liulian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.haomee.adapter.AlbumAdapter;
import com.haomee.entity.Image;

public class AlbumActivity extends BaseActivity {

	private AlbumAdapter imageChoiceAdapter;
	private GridView gridView_images;
	private ImageView bt_back;
	private List<Image> list_image;
	private Image image;
	private File vFile;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String path;
	private Context activity_context;
	
	private String picturePath;
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.ORIENTATION,
			MediaStore.Images.Media.DATE_ADDED };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_images);
		activity_context = this;
		gridView_images = (GridView) findViewById(R.id.gridView_images);
		gridView_images.setOnItemClickListener(myOnItemClickListener);
//		initData();
		selectPicFromLocal(this);
		bt_back = (ImageView) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

/**	public void initData() {

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				list_image = new ArrayList<Image>();
				Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, null, MediaStore.Images.Media.DATE_ADDED + " desc ");				
				if(cursor==null){
					return;
				}
				
				while (cursor.moveToNext()) {
					image = new Image();
					image.setId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
					image.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
					image.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
					image.setMime_type(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)));
					image.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
					list_image.add(image);
				}
				cursor.close();
				imageChoiceAdapter = new AlbumAdapter(AlbumActivity.this);
				imageChoiceAdapter.setData(list_image);
				gridView_images.setAdapter(imageChoiceAdapter);
			}
		});
	}*/
	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal(final Activity context) {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		if(intent!=null){
			context.startActivityForResult(intent, PHOTORESOULT);
		}
//		context.startActivityForResult(intent, PHOTORESOULT);
	}
	
	
	
	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			path = list_image.get(position).getFilePath();
			Intent intent = new Intent();
			intent.putExtra("path", path);
			intent.setClass(activity_context, ImageCropActivity.class);
			startActivityForResult(intent, CROPIMAGES);
		}
	};

	//裁剪
	public void startCrop(String path) {

		Intent intent = new Intent();
		intent.putExtra("path", path);
		intent.setClass(activity_context, ImageCropActivity.class);
		startActivityForResult(intent, CROPIMAGES);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOHRAPH) {
			if (vFile != null && vFile.exists()) {
				startCrop(Uri.fromFile(vFile).getPath());
			}
		}else if (requestCode == CROPIMAGES) {
			if (data != null) {
				path = data.getStringExtra("path");
				Intent intent = new Intent();
				intent.putExtra("path", path);
				intent.putExtra("width", data.getIntExtra("width", 300));
				intent.putExtra("heigth", data.getIntExtra("heigth", 200));
				setResult(20, intent);
				AlbumActivity.this.finish();
			}else {
				finish();
			}
			
		}if(requestCode ==PHOTORESOULT){//打开系统相册进行裁剪图片
			if(data==null){//处理返回，取消键被点击报空指针异常
				finish();
				return;
			}
			Uri startCrop = data.getData();
			if(startCrop!=null){
				findPicByUri(startCrop);
			}
		}
	}
	/**
	 * 根据图库图片uri获取图片
	 */
	private void findPicByUri(Uri selectedImage) {
		Cursor cursor = null;
		if(selectedImage!=null){
			cursor = getContentResolver().query(selectedImage, null, null, null, null);
		}
//		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {//判断图片是否存在
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;
			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		} else {//判断图片是否存在
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
		startCrop(picturePath);//进行图片的裁剪
	}

}
