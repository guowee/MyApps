package com.haomee.liulian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.haomee.adapter.ImageChoiceAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Image;
import com.haomee.util.FileDownloadUtil;

public class SearchImage extends BaseActivity{
	
	private ImageChoiceAdapter  imageChoiceAdapter;
	private GridView gridView_images;
	private ImageView bt_back;
	private List<Image> list_image;
	private Image image;
	private String content_title, content;
	private String topic_id;
	private File vFile;
	private String dir_temp;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String head_pic;
	private String path;
	private Context activity_context;
	private static final String[] STORE_IMAGES = { 
		MediaStore.Images.Media._ID, 
		MediaStore.Images.Media.DATA, 
		MediaStore.Images.Media.TITLE, 
		MediaStore.Images.Media.MIME_TYPE, 
		MediaStore.Images.Media.SIZE, 
		MediaStore.Images.Media.ORIENTATION,
		MediaStore.Images.Media.DATE_ADDED
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_images);
		topic_id = getIntent().getStringExtra("topic_id");
		content_title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		
		activity_context = this;
		dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
		
		gridView_images =  (GridView) findViewById(R.id.gridView_images);
		gridView_images.setOnItemClickListener(myOnItemClickListener);
		initData();
		
		bt_back =  (ImageView) findViewById(R.id.bt_back);
		
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	public void initData(){
		
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				list_image = new ArrayList<Image>();
				Image image1 = new Image();
				image1.setId(String.valueOf(R.drawable.camera_button_camera));
				Image image2 = new Image();
				image2.setId(String.valueOf(R.drawable.camera_button_album));

				list_image.add(0, image1);
				list_image.add(1, image2);
				
				Cursor cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						STORE_IMAGES,
						null,null,
		                MediaStore.Images.Media.DATE_ADDED+ " desc ");
				
				if(cursor==null){
					return;
				}
				
				while (cursor.moveToNext()) {
					
					if(cursor.getCount() == 18)
						break;
					
					image = new Image();
					image.setId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
					image.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
					image.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
					image.setMime_type(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)));
					image.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
					list_image.add(image);
				}
				cursor.close();
				imageChoiceAdapter  = new ImageChoiceAdapter(SearchImage.this);
				imageChoiceAdapter.setData(list_image);
				gridView_images.setAdapter(imageChoiceAdapter);
			}
		});
	}
	
	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
				vFile = new File(dir_temp + "choice_image.jpg");
				Uri uri = Uri.fromFile(vFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, PHOTOHRAPH);
			} else if (position == 1) {

				try{
					Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent2, PHOTOZOOM);
				}catch(Exception e){
					//Toast.makeText(context, "打开相册失败", Toast.LENGTH_SHORT).show();
					Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
					startActivityForResult(intent2, PHOTOZOOM);
				}
			} else {
				path = list_image.get(position).getFilePath();
				Intent intent = new Intent();
				intent.putExtra("path", path);
				intent.setClass(activity_context, ImageCropActivity.class);
				startActivityForResult(intent, CROPIMAGES);
			}
		}
	};
	

	//裁剪
	public void startCrop(String path){
		
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
		} else if (requestCode == PHOTOZOOM) {
			if (data != null) {
				// 读取相册缩放图片
				Uri originalUri = data.getData();
				if (originalUri != null) {
					String[] proj = { MediaStore.Images.Media.DATA };
					@SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(originalUri, proj, null, null, null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(column_index);
					startCrop(path);
				}
			}
		} else if (requestCode == CROPIMAGES) {
			if (data != null) {
				path  = data.getStringExtra("path");
				byte[] bis = data.getByteArrayExtra("bitmap");
				Intent intent = new Intent();
				intent.putExtra("topic_id", topic_id);
				intent.putExtra("title", content_title);
				intent.putExtra("content", content);
				intent.putExtra("path", path);
				intent.putExtra("bitmap", bis);
				intent.putExtra("width", data.getIntExtra("width",300));
				intent.putExtra("heigth", data.getIntExtra("heigth",200));
				intent.setClass(activity_context, SendImageContent.class);
				startActivity(intent);
			}
		}

	}
}
