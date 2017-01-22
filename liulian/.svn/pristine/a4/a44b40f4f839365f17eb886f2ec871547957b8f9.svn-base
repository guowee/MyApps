package com.haomee.liulian;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.haomee.adapter.ImageChoiceAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Image;
import com.haomee.liulian.upyun.UpYunException;
import com.haomee.liulian.upyun.UpYunUtils;
import com.haomee.liulian.upyun.Uploader;
import com.haomee.util.FileDownloadUtil;

//图片选择
public class ImageChoiceActivity extends BaseActivity {

	private GridView gridView_images;
	private String dir_temp;
	private File vFile;
	public File tempFile;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String head_pic;
	private Image image;
	private List<Image> list_image;
	private ImageChoiceAdapter  imageChoiceAdapter;

//	// 设置获取图片的字段信�?
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
		dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
		gridView_images =  (GridView) this.findViewById(R.id.gridView_images);
		
		list_image  = new ArrayList<Image>();
		Cursor cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				STORE_IMAGES,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[] {"%LC/images%"},  
                MediaStore.Images.Media.DATE_ADDED + "desc");
		
		
		while (cursor.moveToNext()) {
			image  = new Image();
			image.setId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
			image.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			
			Log.e("data", cursor.getString(cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATE_ADDED))+"");
			
			image.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
			image.setMime_type(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)));
			image.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
			list_image.add(image);
		}
		cursor.close();
		imageChoiceAdapter.setData(list_image);
		gridView_images.setAdapter(imageChoiceAdapter);
		
	
	}

	public OnClickListener btItemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_take_photo:
				vFile = new File(dir_temp + "add_image.jpg");
				Uri uri = Uri.fromFile(vFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, PHOTOHRAPH);
				break;
			case R.id.btn_pick_photo:

				try{
					Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent2, PHOTOZOOM);
				}catch(Exception e){
					//Toast.makeText(context, "打开相册失败", Toast.LENGTH_SHORT).show();
					Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
					startActivityForResult(intent2, PHOTOZOOM);
				}
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTOHRAPH) {
			if (vFile != null && vFile.exists()) {
				startPhotoZoom(Uri.fromFile(vFile));
			}
		} else if (requestCode == PHOTOZOOM) {
			if (data != null) {
				// 读取相册缩放图片
				Uri originalUri = data.getData();
				if (originalUri != null) {
					startPhotoZoom(originalUri);
					String[] proj = { MediaStore.Images.Media.DATA };
					@SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(originalUri, proj, null, null, null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(column_index);
					new ImageUploadTask().execute(path);
				}
			}

		} else if (requestCode == PHOTORESOULT) {
			if (data != null) {
				Bundle extras = data.getExtras();
				Uri originalUri = data.getData();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
					new ImageUploadTask().execute(tempFile.getAbsolutePath());
				}
			}
		}
	}
	
	


	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 64);
		intent.putExtra("outputY", 64);
		intent.putExtra("return-data", true);
		tempFile = new File(dir_temp + Calendar.getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
		intent.putExtra("output", Uri.fromFile(tempFile)); // 专入目标文件
		intent.putExtra("outputFormat", "JPEG"); // 输入文件格式
		startActivityForResult(intent, PHOTORESOULT);
	}

	class ImageUploadTask extends AsyncTask<String, Void, String> {
		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间

		@Override
		protected String doInBackground(String... arg0) {
			String string = null;
			try {
				String SAVE_KEY = File.separator + "haomee" + File.separator + System.currentTimeMillis() + ".jpg";
				head_pic = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
				Log.e("----图片地址---", head_pic + "");
				String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
				String signature = UpYunUtils.signature(policy + "&" + TEST_API_KEY);
				string = Uploader.upload(policy, signature, BUCKET, arg0[0]);
			} catch (UpYunException e) {
				e.printStackTrace();
			}
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {

			} else {

			}
		}
	}

}
