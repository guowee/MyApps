package com.hipad.smart.local.msg;


public class Msg {	
	/**
	 * msg type
	 * @author wangbaoming
	 *
	 */
	public final static class Type {
		public final static int UNKNOWN = -1;
		public final static int CMD = 0x10; 
		public final static int REPONCE = 0x11;
		public final static int REPORT = 0x12;
	}	
	private int mType;
	
	private long mNo;
	private int mCmd;
	private String mDeviceId;
	private boolean mSuccessful;
	private byte[] mArgs;
	
	private String mTargetIp;
	
	public Msg() {
	}
	
	public Msg(int type, int cmd, String deviceId, boolean successful, String args) {		
		mType = type;
		mCmd = cmd;
		mDeviceId = deviceId;
		mSuccessful = successful;
		mArgs = args.getBytes();
	}
	
	public int getType() {
		return mType;
	}
	public long getNo() {
		return mNo;
	}
	public int getCmd() {
		return mCmd;
	}
	public String getDevice() {
		return mDeviceId;
	}
	public boolean isSuccessful() {
		return mSuccessful;
	}
	public byte[] getArgs() {
		return mArgs;
	}
	public String getTargetIp() {
		return mTargetIp;
	}
	
	
	public void setType(int type) {
		mType = type;
	}
	public void setNo(long no) {
		mNo = no;
	}
	public void setCmd(int cmd) {
		mCmd = cmd;
	}
	public void setDevice(String device) {
		mDeviceId = device;
	}
	public void setSuccessful(boolean successful) {
		mSuccessful = successful;
	}
	public void setArgs(byte[] args) {
		mArgs = args;
	}
	public void setTargetIp(String targetIp) {
		mTargetIp = targetIp;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("type: " + mType)
			.append(", no: " + mNo)
			.append(", cmd: " + mCmd)
			.append(", device: " + mDeviceId)
			.append(", successful: " + mSuccessful)
			.append(", args: " + mArgs)
			.append(", ip: " + mTargetIp);
		return sb.toString();
	}
}
