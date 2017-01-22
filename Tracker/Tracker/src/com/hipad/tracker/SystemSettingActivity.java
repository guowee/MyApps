package com.hipad.tracker;



import java.io.File;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.xmlpull.v1.XmlPullParser;

import com.hipad.tracker.config.WebService;
import com.hipad.tracker.dialog.LogoutDialog;
import com.hipad.tracker.dialog.LogoutDialog.OnLogoutDialogListener;
import com.hipad.tracker.dialog.PeriodPopWindow;
import com.hipad.tracker.dialog.PeriodPopWindow.PeriodChoseListener;
import com.hipad.tracker.dialog.ResetDialog;
import com.hipad.tracker.dialog.ResetDialog.OnResetDialogListener;
import com.hipad.tracker.dialog.UpdateDialog;
import com.hipad.tracker.dialog.UpdateDialog.OnUpdatDialogListener;
import com.hipad.tracker.entity.AppVersion;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.CommonResponse;
import com.hipad.tracker.update.AppNotificationControl;
import com.hipad.tracker.widget.WiperSwitch;
import com.hipad.tracker.widget.WiperSwitch.OnToggleStateChangeListener;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest.Priority;
/**
 * 
 * @author guowei
 *
 */
public class SystemSettingActivity extends BaseActivity implements	OnClickListener,OnToggleStateChangeListener {
	public static final String TAG=SystemSettingActivity.class.getSimpleName();
	private ImageButton leftBtn;
	private TextView titleText;
    private Context mContext;
	private TextView versionTxt;
	private Button btnLogout;
    private WiperSwitch wiperBtn;
   // private DownloadManager mDownloadManager ;  
	//private DownloadCompleteReceiver mCompleteReceiver;
	
	private TextView periodTxt;
	private RelativeLayout checkUpdateView,addTrackerView,resetFactoryView;
	String[] items=new String[]{"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
	
    private ResetDialog mDialog;
    private String imeiStr;
    private static String bizSn;
    private static String bizSpn2;

    private FactoryHandler fHandler;
    private FacResultHandler frHandler;
    private CurfewHandler cHandler;
    private CurfewResultHandler crHandler;
    
    private List<AppVersion> appList=new ArrayList<AppVersion>();
    private int serverCode;
    
    private File destDir = null;
    private File destFile = null;
    private String mDownloadUrl = null;
    
    private ThinDownloadManager downloadManager;
	// 下载的线程数
	private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
	MyDownloadListener myDownloadStatusListener = new MyDownloadListener();

	int downloadId1 = 0;
	private DownloadRequest request1;
	AppNotificationControl notificationControl;
	private static final String FILE1 = WebService.UPDATE_SERVER+WebService.UPDATE_APKNAME;
	
	
    private boolean isCurfew=false;
    private boolean isCancel=false;
    private String peroidStr;
	private String wakeTime;
	private String sleepTime;
    private static final int MSG_FAC=0x01;
    private static final int MSG_CUR=0x02;
    private static final int MSG_CANCEL_CUR=0x03;
    private static final int MSG_SUCCESS=0x04;
    private static final int MSG_CUR_RESULT=0x05;
    private static final int DOWNLOAD_FAIL=0x06;
    
	
    @SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
    	
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_FAC:
				service.facResult(imeiStr, MyApplication.account,bizSn, frHandler);
				break;
			case MSG_CUR:
				service.curfew(MyApplication.account, MyApplication.imei, "1",wakeTime,sleepTime, cHandler);
				break;
			case MSG_CANCEL_CUR:
				service.curfew(MyApplication.account,MyApplication.imei, "0", "0000","0000", cHandler);
				break;
				
			case MSG_SUCCESS:
				
				for (AppVersion version : appList) {
					serverCode=version.getVerCode();
				}
				if(serverCode>getVersionCode()){
					
					UpdateDialog dialog=new UpdateDialog(mContext, R.style.MyDialog, new OnUpdatDialogListener(){

						@Override
						public void OnClick(View v) {
							/*Intent intent = new Intent(mContext,AppUpgradeService.class);  
							intent.putExtra("downloadUrl", WebService.UPDATE_SERVER+WebService.UPDATE_APKNAME);
							startService(intent);*/
							
							 if (downloadManager.query(downloadId1) == DownloadManager.STATUS_NOT_FOUND) {
				                    downloadId1 = downloadManager.add(request1);
				                }
				                notificationControl.showProgressNotify();
							
						}});
					dialog.show();
				}else{
					//showNotifyDialog(mContext, getString(R.string.soft_update_latest));
					showCustomToast(mContext, getString(R.string.soft_update_latest));
				}
				break;
			case MSG_CUR_RESULT:
				service.curfewResult(MyApplication.account,	MyApplication.imei, bizSpn2,crHandler);
				break;
			case DOWNLOAD_FAIL:
				showCustomToast(mContext,"Update failed !");
				break;
			default:
				break;
			}
    		
    	};
    	
    };
    
   /* public void download(){
    	try {
			  //创建下载请求  
           DownloadManager.Request request=new DownloadManager.Request (Uri.parse(mDownloadUrl));  
           //设置允许使用的网络类型，这里是移动网络和wifi都可以  
           request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
           //发出通知，既后台下载  
           request.setShowRunningNotification(true);  
           //不显示下载界面  
           request.setVisibleInDownloadsUi(false);  
           //设置下载后文件存放的位置  
           request.setDestinationInExternalPublicDir(destDir.getPath(), WebService.UPDATE_APKNAME);
           //将下载请求放入队列  
           mDownloadManager.enqueue(request);  
       } catch (Exception e) {
           Message msg1 = mHandler.obtainMessage();
           msg1.what = DOWNLOAD_FAIL;
           mHandler.sendMessage(msg1);
           e.printStackTrace();
       }
    }*/
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_layout);
		mContext=this;
		createSDCardDir(WebService.UPDATE_APKNAME);
		getViews();
		initial();
		getData();
		setClick();
	}
	@Override
	protected void onResume() {

		//registerReceiver(mCompleteReceiver,  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		super.onResume();
	}
	
	
	private void initial(){
		downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
		Uri downloadUri = Uri.parse(FILE1);
		Uri destinationUri = Uri.parse(urlPath);
		request1 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(Priority.HIGH)
				.setDownloadListener(myDownloadStatusListener);
		notificationControl = new AppNotificationControl(this.urlPath);
		
	}
	
	
	
	private void getData(){
		fHandler=new FactoryHandler();
		frHandler=new FacResultHandler();
		cHandler=new CurfewHandler();
		crHandler=new CurfewResultHandler();
		
		/* //获取下载服务  
		mDownloadManager =(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);  
		mCompleteReceiver=new DownloadCompleteReceiver();*/
		
		String startTime=sph.getString("info_startTime");
		String endTime=sph.getString("info_endTime");
		String status=sph.getString("info_status");
		
		if("0".equals(status)){
			//Not Curfew
			periodTxt.setText("");
			wiperBtn.setToggleState(false);
		}
		if("1".equals(status)){
			//Curfew 
			periodTxt.setText(insertChar2String(startTime, ":", 2)+"-"+insertChar2String(endTime, ":", 2));
			wiperBtn.setToggleState(true);
		}
	
	}
	
	private void getViews() {
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.VISIBLE);
		titleText = (TextView) findViewById(R.id.titleTxt);
		titleText.setText(getString(R.string.system_settings));
		btnLogout=(Button) findViewById(R.id.btn_logout);
		versionTxt=(TextView) findViewById(R.id.versionTxt);
		versionTxt.setText(getTrackerVersion());
		periodTxt=(TextView) findViewById(R.id.disturb_period);
		wiperBtn=(WiperSwitch) findViewById(R.id.curfew_rightbtn);
		checkUpdateView=(RelativeLayout) findViewById(R.id.check_update_view);
		addTrackerView=(RelativeLayout) findViewById(R.id.add_device_view);
		resetFactoryView=(RelativeLayout) findViewById(R.id.reset_factory_view);
	}


	public String insertChar2String(String a,String b,int t){
		return a.substring(0,t)+b+a.substring(t,a.length());
	}
	
	public String getTrackerVersion(){
		String version="";
		version=sph.getString("info_version");
		return version;
	}
	
	private void setClick(){
		leftBtn.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		checkUpdateView.setOnClickListener(this);
		addTrackerView.setOnClickListener(this);
		resetFactoryView.setOnClickListener(this);
		wiperBtn.setOnToggleStateChangeListener(this);
	}
	private List<Map<String,Object>> initItems(){
		 List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();  
		 String[] titles={getResources().getString(R.string.add_tracker),getResources().getString(R.string.factory_reset)};
		 for(int i = 0; i < titles.length; i++) {   
	            Map<String, Object> map = new HashMap<String, Object>();    
	            map.put("image", R.drawable.ico_enter);   //图片资源   
		        map.put("title", titles[i]);              //标题   
		        listItems.add(map);   
		       }   
		return listItems;
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.btn_logout:
			final LogoutDialog dialog=new LogoutDialog(mContext, R.style.MyDialog, new OnLogoutDialogListener() {
				
				@Override
				public void OnLogoutClick() {
					startActivity(new Intent(mContext,LoginActivity.class));
					finish();
				}
			});
			dialog.show();
			break;
		case R.id.no_disturb_view:
			break;
			
		case R.id.check_update_view:
			new Thread(runnable).start();
			break;
		case R.id.add_device_view:
			startActivity(new Intent(mContext, ImeiNumberActivity.class));
			break;
		case R.id.reset_factory_view:
			mDialog=new ResetDialog(mContext, R.style.MyDialog, new OnResetDialogListener() {
				@Override
				public void OnResetClick(String imei) {
					imeiStr=imei;
					service.factory(imeiStr, MyApplication.account, fHandler);
			}
			});
			mDialog.show();
			break;
			
		default:
			break;
		}

	}

	@Override
	public void onToggleStateChange(boolean flag) {
		if (flag) {
			PeriodPopWindow popWindow = new PeriodPopWindow(mContext, items);
			popWindow.setPeriodChoseListener(new PeriodChoseListener() {
				@Override
				public void onPeriodChoose(int start, int end) {
					Log.i("TAG",start+","+end);
					
					if (start == end) {
						//showNotifyDialog(mContext,getString(R.string.over_time_period));
						showCustomToast(mContext, getString(R.string.same_params));
						wiperBtn.setToggleState(false);
					}/*else if (start == end) {
						showNotifyDialog(mContext,getString(R.string.same_params));
						wiperBtn.setToggleState(false);
					}*/ else {
						StringBuffer sb = new StringBuffer();
						sb.append(items[start] + "-" + items[end]);
						periodTxt.setText(sb.toString());
						periodTxt.setVisibility(View.VISIBLE);
						sph.putString("period", sb.toString());
						wakeTime = removeChar(items[start], ":");
						sleepTime = removeChar(items[end], ":");
						mHandler.sendEmptyMessage(MSG_CUR);
					}
				}

				@Override
				public void onPeriodCancel(boolean flag) {
					isCancel=flag;
					if(isCancel){
						wiperBtn.setToggleState(false);
					}
				}
			});
			popWindow.showPopupWindow();
			
		} else {
			
			mHandler.sendEmptyMessage(MSG_CANCEL_CUR);
			periodTxt.setVisibility(View.INVISIBLE);
		}
	}
	
	public int getVersionCode() {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	public String removeChar(String s,String c){
		int index=s.indexOf(c);
		return s.substring(0, index)+s.substring(index+1);
	}
	
	private  class  FactoryHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						
						if(getString(R.string.factory_request_complete).equals(response.getMsg())){
							bizSn=response.getBizSN();
							service.facResult(imeiStr, MyApplication.account,response.getBizSN(), frHandler);
						}
						
						if(getString(R.string.factory_success).equals(response.getMsg())){
							//showToastLong(getString(R.string.reset_success));
							showCustomToast(mContext, getString(R.string.reset_success));
						}
					}else{
						execute(mContext, response.getMsg());
						if(getString(R.string.params_error).equals(response.getMsg())){
							//showNotifyDialog(mContext, getString(R.string.imei_error));
							showCustomToast(mContext, getString(R.string.imei_error));
						}
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	private class FacResultHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {
			
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						if (getString(R.string.facResult_request_complete)
								.equals(response.getMsg())) {
							mHandler.sendEmptyMessageDelayed(MSG_FAC, 3000);
						}
						if (getString(R.string.facResult_success).equals(
								response.getMsg())) {
							//showNotifyDialog(mContext, getString(R.string.reset_success));
							showCustomToast(mContext, getString(R.string.reset_success));
						}
					} else {
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	private class CurfewHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{
		@Override
		public void handle(boolean timeout, CommonResponse response) {
			if(!timeout){
				if(response!=null){
					if (response.isSuccessful()) {
					if(getString(R.string.curfew_request_complete).equals(response.getMsg())){
						bizSpn2=response.getBizSN();
						service.curfewResult(MyApplication.account, MyApplication.imei, response.getBizSN(), crHandler);
					}
					if(getString(R.string.curfew_success).equals(response.getMsg())){
						showToastShort(response.getMsg());
					}
					}else{
						execute(mContext, response.getMsg());	
					}
				}else{
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	private class CurfewResultHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {
			if(!timeout){
				if(response!=null){
					if (response.isSuccessful()) {
						if (getString(R.string.curfewResult_request_complete).equals(response.getMsg())) {
							mHandler.sendEmptyMessageDelayed(MSG_CUR_RESULT, 3000);
						}
						if (getString(R.string.curfewResult_success).equals(response.getMsg())) {
							
							sph.putString("user", MyApplication.user.getName());
							if("1".equals(response.getEnable())){
								sph.putBoolean("isCurfew", true);
								//showNotifyDialog(mContext, getString(R.string.set_curfew_success));
								showCustomToast(mContext, getString(R.string.set_curfew_success));
								Log.i("success",getString(R.string.set_curfew_success));
								
							}
							if("0".equals(response.getEnable())){
								//close
								sph.putBoolean("isCurfew",false);
								//showNotifyDialog(mContext, getString(R.string.close_curfew_success));
								showCustomToast(mContext, getString(R.string.close_curfew_success));
								Log.i("success",getString(R.string.close_curfew_success));
							}
						}
					} else {
						execute(mContext, response.getMsg());
					}
				}else{
					showToastShort(getString(R.string.neterror_hint));
				}
				
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
/*	private class DownloadCompleteReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			   if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){  
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
                Log.v(TAG," download complete! id : "+downId); 
               // install(destFile);
            }  else if(DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())){
                long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                //点击通知栏取消下载
                mDownloadManager.remove(ids);
                showCustomToast(mContext, "Download Cancel !");
              }
		}
	}*/
	
	Runnable runnable=new Runnable() {
		
		@Override
		public void run() {
			  //1:确定地址  
            String path = WebService.UPDATE_SERVER+WebService.UPDATE_VERJSON;  
            try {  
                URL url = new URL(path);  
                //建立连接  
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
                //设置请求方式  
                conn.setRequestMethod("GET");  
                //设置请求超时时间  
                conn.setConnectTimeout(5000);  
                //设置读取超时时间  
                conn.setReadTimeout(5000);  
                //判断是否获取成功  
                if(conn.getResponseCode() == 200)  
                {  
                    //获得输入流  
                    InputStream is = conn.getInputStream();  
                    //解析输入流中的数据  
                    appList= parseXmlInfo(is);  
                    mHandler.sendEmptyMessage(MSG_SUCCESS);
                }  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
	};
	
	public List<AppVersion> parseXmlInfo(InputStream is){
		 //1.先拿到pull解析器  
       XmlPullParser xParser = Xml.newPullParser();  
       List<AppVersion> appList=null;
       try {  
             
           xParser.setInput(is, "utf-8");  
           //获取事件的类型  
           int eventType = xParser.getEventType();  
           AppVersion appInfo = null;  
           while(eventType != XmlPullParser.END_DOCUMENT)  
           {  
               switch (eventType) {  
               case XmlPullParser.START_TAG:  
                   if("apps".equals(xParser.getName()))  
                   {  
                       appList = new ArrayList<AppVersion>();  
                   }
                   else if ("app".equals(xParser.getName())) {  
                       appInfo = new AppVersion();  
                   }  
                   else if ("appname".equals(xParser.getName())) {  
                       String appname = xParser.nextText();  
                        appInfo.setAppname(appname);  
                   }  
                   else if ("apkname".equals(xParser.getName())) {  
                       String apkname = xParser.nextText();  
                       appInfo.setApkname(apkname); 
                   }  
                   else if ("verName".equals(xParser.getName())) {  
                       String verName = xParser.nextText();  
                      appInfo.setVerName(verName);
                   }  
                   else if ("verCode".equals(xParser.getName())) {  
                       String verCode = xParser.nextText();  
                       appInfo.setVerCode(Integer.valueOf(verCode));  
                   }  
                   break;  
               case XmlPullParser.END_TAG:  
                   //当结束时间是news时，说明一条news已经解析完成，并且加入到集合中  
                   if("apps".equals(xParser.getName()))  
                   {  
                       appList.add(appInfo);  
                   }  
                   break;  
               }  
               eventType = xParser.next();  
           }  
       } catch (Exception e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
       return appList;
	}
	
    public boolean checkApkFile(String apkFilePath) {
        boolean result = false;
        try{
            PackageManager pManager = getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result =  false;
            } else {
                result =  true;
            }
        } catch(Exception e) {
            result =  false;
            e.printStackTrace();
        }
        return result;
    }

    public void install(File apkFile){
        Uri uri = Uri.fromFile(apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private String getBytesDownloaded(int progress, long totalBytes) {
		// Greater than 1 MB
		long bytesCompleted = (progress * totalBytes) / 100;
		if (totalBytes >= 1000000) {
			return (""
					+ (String.format("%.1f", (float) bytesCompleted / 1000000))
					+ "/"
					+ (String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
		}
		if (totalBytes >= 1000) {
			return ("" + (String.format("%.1f", (float) bytesCompleted / 1000))
					+ "/" + (String.format("%.1f", (float) totalBytes / 1000)) + "Kb");

		} else {
			return ("" + bytesCompleted + "/" + totalBytes);
		}
	}

	String urlPath;

	public String createSDCardDir(String name) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/download";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir + File.separator + name);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (Exception e) {
				}
			}
			urlPath = file.getPath();
		}
		return urlPath;
	}

	
	@Override
	protected void onDestroy() {

		/*if(mCompleteReceiver!=null){
			unregisterReceiver(mCompleteReceiver);
		}*/
		downloadManager.release();
		super.onDestroy();
	}
	
	class MyDownloadListener implements DownloadStatusListener {

		@Override
		public void onDownloadComplete(int id) {
		}

		@Override
		public void onDownloadFailed(int id, int errorCode, String errorMessage) {
		}

		@Override
		public void onProgress(int id, long totalBytes, long downloadedBytes,
				int progress) {
			 if (id == downloadId1) {
	                // setNotify(progress);
	                notificationControl.updateNotification(progress);
	            }

		}

	}
	
	
	
	
}
