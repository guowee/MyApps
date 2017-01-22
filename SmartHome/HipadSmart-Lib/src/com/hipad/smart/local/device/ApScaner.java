/**
 * ApScaner.java 2014-9-11
 */
package com.hipad.smart.local.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.R.bool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author wangbaoming
 * 
 */
public class ApScaner {
	private final static String TAG = "ApScaner";
	
	private Context mContext;
	private WifiManager mWifiMgr;
	
	private ApScanReceiver mApScanReceiver;
	private WifiStateReceiver mWifiStateReceiver;	
	
	private ArrayList<ScanStateListener> mScanStateListeners;
	private ArrayList<ConnectStateChangeListener> mConnectStateChangeListeners;
	
	/* the previous wifi state enable or disable, maybe the state should be restored after exit */
	private boolean mPreviousState = false;
	private boolean mWifiConnected = false;
	
	public ApScaner(Context context) {
		mContext = context;
		
		mScanStateListeners = new ArrayList<ScanStateListener>();
		mConnectStateChangeListeners = new ArrayList<ConnectStateChangeListener>();
		
		mWifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mApScanReceiver = new ApScanReceiver();
		mWifiStateReceiver = new WifiStateReceiver();
		
		init();
	}
	
	private void init(){
		mPreviousState = mWifiMgr.isWifiEnabled();
		if(!mPreviousState){
			mWifiMgr.setWifiEnabled(true);
		}
		
		mContext.registerReceiver(mApScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		
		mContext.registerReceiver(mWifiStateReceiver, intentFilter);
	}
	
	public void release(){
		mContext.unregisterReceiver(mWifiStateReceiver);
		mContext.unregisterReceiver(mApScanReceiver);
	}
	
	public void addScanStateListener(ScanStateListener listener){
		if(!mScanStateListeners.contains(listener)) mScanStateListeners.add(listener);
	}	
	
	public void removeScanStateListener(ScanStateListener listener){
		mScanStateListeners.remove(listener);
	}
	
	public void addConnectStateChangeListener(ConnectStateChangeListener listener){
		mConnectStateChangeListeners.add(listener);
	}
	
	public void removeConnectStateChangeListener(ConnectStateChangeListener listener){
		mConnectStateChangeListeners.remove(listener);
	}

	public void scanAp() {		
		mWifiMgr.startScan();
	}
	
	public WifiInfo getCurrentAp(){
		WifiInfo wifiInfo = mWifiMgr.getConnectionInfo();
		return wifiInfo;
	}
	
	public boolean isWifiConnected(){
		return mWifiConnected;
	}
	
	public boolean connect(ScanResult ap, String pwd){		
		return connectTarget(ap.SSID, pwd);
	}
	
	public boolean connectTarget(String ssid, String pwd){
		boolean successful = false;
    	ArrayList<WifiConfiguration> confifuredAps = (ArrayList<WifiConfiguration>) mWifiMgr.getConfiguredNetworks();
    	if(null == confifuredAps) return false; // if Wi-Fi is turned off, it can be null.
    	
    	boolean found = false;
    	WifiConfiguration ap = null;
    	for(int i = 0; i < confifuredAps.size(); i++){
    		ap = confifuredAps.get(i);
    		if(ap.SSID.equals("\""+ssid+"\"")){
    			found = true;
    			break;
    		}
    	}
    	
    	if(found){
    		successful = connectNetWork(ap.networkId,  ap.SSID);
    	}else{    		            
            int wifiId =  addConfig(ssid, pwd);    		

            if (wifiId != -1/* && mWifiMgr.saveConfiguration()*/) {
//            	ArrayList<WifiConfiguration> confifuredAps = (ArrayList<WifiConfiguration>) mWifiMgr.getConfiguredNetworks();
            	successful = connectNetWork(wifiId, ssid);
            }
    	}
    	
    	return successful;
	}
	
    private boolean connectNetWork(int networkId, String ssid){
    	return mWifiMgr.enableNetwork(networkId, true);
    }
    
    private int addConfig(String ssid,String password){
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";
        if(password != null && !password.equals("")){
        	wc.preSharedKey = "\"" + password + "\"";
        }else{
        	wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }        
        
        return mWifiMgr.addNetwork(wc);
    }
	
    public boolean removeAp(int networkId){
    	return mWifiMgr.removeNetwork(networkId);
    }
    
	class ApScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction(); 
			if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){

				List<ScanResult> apList = mWifiMgr.getScanResults();
				
				StringBuilder sb = new StringBuilder();

                for (int i = 0; i < apList.size(); i++) {

                        sb.append(new Integer(i).toString() + ".");

                        sb.append((apList.get(i)).toString());

                        sb.append("\n\n");
                }
                
                Log.i(TAG, "ap list:" + sb.toString());
                
                notifyScanCompleted(apList);
			}
			
		}
		
	}
	
	class WifiStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction(); 
			if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
				int prevState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				if(WifiManager.WIFI_STATE_ENABLED == state && WifiManager.WIFI_STATE_ENABLED != prevState){
					Log.i(TAG, "wifi enabled");
				}else if(WifiManager.WIFI_STATE_DISABLED == state && WifiManager.WIFI_STATE_DISABLED != prevState){
					Log.i(TAG, "wifi disabled");				
				}
			}else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(NetworkInfo.State.CONNECTED.equals(networkInfo.getState()) ){
					Log.i(TAG, "wifi connected");
					mWifiConnected = true;
					WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
					notifyConnectStateChanged(true, wifiInfo);
				}else if(NetworkInfo.State.DISCONNECTED.equals(networkInfo.getState())){
					Log.i(TAG, "wifi disconnected");
					mWifiConnected = false;
					notifyConnectStateChanged(false, null);
				}
			}
			
		}
		
	}
	
	private void notifyScanCompleted(List<ScanResult> apList){
		if(null != mScanStateListeners){
			for(ScanStateListener listener : mScanStateListeners){
				listener.onScanCompleted(apList);
			}
        }
	}
	
	private void notifyConnectStateChanged(boolean connected, WifiInfo info){
		if(null != mConnectStateChangeListeners){
			for (ConnectStateChangeListener listener : mConnectStateChangeListeners) {
				listener.onConnectStateChanged(connected, info);
			}
		}
	}
	
	public static interface ScanStateListener{
		public void onScanCompleted(List<ScanResult> apList);
	}
	
	public static interface ConnectStateChangeListener {
		/**
		 * notify the wifi has connected.
		 * @param connected
		 * 		true if connected, false otherwise
		 * @param info
		 * 		the {@link WifiInfo} if connected, null if disconnected
		 */
		public void onConnectStateChanged(boolean connected, WifiInfo info);
	}
}
