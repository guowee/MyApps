/**
 * $(#)DeviceProtocolV15.java
 */
package com.hipad.smart.local.msg;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.graphics.YuvImage;

/**
 * @author wangbaoming
 *
 */
public class DeviceProtocolV15 extends Protocol {
	public final static String DEVICE_PROTOCOL_V15 = "DEVICE_PROTOCOL_V1.5";
	
	/* the data index */
	public final static int INDEX_CMD = 0;
	public final static int INDEX_SIZE = 2;
	public final static int INDEX_NO = 4;	
	public final static int INDEX_VER = 8;	
	public final static int INDEX_TYPE = 9;
	public final static int INDEX_DEV = 10;
	public final static int INDEX_FLAG = 26;
	public final static int INDEX_ARGS = 27;
	/* the data index */

	/* (non-Javadoc)
	 * @see com.hipad.msg.Parser#parseDataPack(java.net.DatagramPacket)
	 */
	@Override
	public Msg parseDataPack(DatagramPacket pack) {
		Msg msg = new Msg();
		
		String ip = pack.getAddress().getHostAddress();
		msg.setTargetIp(ip);		

		byte[] data = pack.getData();
		parseMsgFromDataBytes(data, msg);
		
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.hipad.msg.Parser#parseDataPack(byte[], java.lang.String)
	 */
	@Override
	public Msg parseDataPack(byte[] data, String ip) {
		Msg msg = new Msg();
		msg.setTargetIp(ip);
		parseMsgFromDataBytes(data, msg);
		
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.hipad.msg.Parser#createDataPack(com.hipad.msg.Msg)
	 */
	@Override
	public byte[] createDataPack(Msg msg) {
		Builder builder = new Builder();
		builder.setNo(msg.getNo())
			.setType(msg.getType())
			.setCmd(msg.getCmd())
			.setDev(msg.getDevice())
			.setResponceCode(msg.isSuccessful() ? 0 : 1)
			.setArgs(msg.getArgs());
		
		return builder.build();
	}

	/* (non-Javadoc)
	 * @see com.hipad.msg.Parser#createDataPack(com.hipad.msg.Msg, int)
	 */
	@Override
	public DatagramPacket createDataPack(Msg msg, int port) {
		Builder builder = new Builder();
		
		builder.setNo(msg.getNo())
			.setType(msg.getType())
			.setCmd(msg.getCmd())
			.setDev(msg.getDevice())
			.setResponceCode(msg.isSuccessful() ? 0 : 1)
			.setArgs(msg.getArgs());
		
		return builder.build(msg.getTargetIp(), port);
	}
	
	private static class Builder extends Protocol.Builder{
		private byte[] mHeader;	
		private byte[] mParams;
		private byte mChecksum;
		
		public Builder() {
			mHeader = new byte[INDEX_ARGS];
		}

		@Override
		public byte[] build() {			
			return getBytes();
		}

		@Override
		public DatagramPacket build(String ip, int port) {			
			byte[] bytes = getBytes();
			
			DatagramPacket dataPack = null;
			try {
				InetAddress inetAddress = InetAddress.getByName(ip);
				dataPack = new DatagramPacket(bytes, bytes.length, inetAddress, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			return dataPack;
		}	
		
		public Builder setType(int type){ 
			mHeader[INDEX_TYPE] = (byte) (type & 0xFF);
			return this;
		}
		
		public Builder setNo(long no){
			final int noLen = 4;
			for(int i = 0; i < noLen; i++){
				mHeader[INDEX_NO + i] = (byte) ((no >> 8 * (noLen - i - 1)) & 0xFF); 
			}			
			return this;
		}
		
		public Builder setCmd(int cmd){
			mHeader[INDEX_CMD] = (byte) ((cmd >> 8) & 0xFF);
			mHeader[INDEX_CMD + 1] =  (byte) (cmd & 0xFF);
			return this;
		}
		
		public Builder setDev(String devId){
			byte[] id = devId.getBytes();
			for(int i = 0; i < 16; i++){
				mHeader[INDEX_DEV + i] = id[i];
			}
			return this;
		}
		
		public Builder setResponceCode(int err){
			mHeader[INDEX_FLAG] = (byte) (err & 0xFF);
			return this;
		}
		
		public Builder setArgs(byte[] args){
			mParams = args;
			return this;
		}
		
		private void generateCheckInfo(byte[] data){			
			// calc the length
			data[INDEX_SIZE] = (byte) (data.length >> 8 & 0xFF); 
			data[INDEX_SIZE + 1] = (byte) (data.length & 0xFF);
			
			// version info
			data[INDEX_VER] = 0x01;
			
			// generate checksum
			byte checksum = 0;
			for(int i = 0; i < data.length - 1; i++){
				checksum += data[i];
			}
			
			checksum = (byte) (~ checksum + 1);
			
			data[data.length - 1] = checksum;			
		}
		
		private byte[] getBytes(){	
			byte[] data = new byte[INDEX_ARGS + mParams.length + 1];
			
			int count = 0;
			for(; count < INDEX_ARGS; count++){
				data[count] = mHeader[count];
			}
			
			for(int i = 0; i < mParams.length; i++){
				data[count++] = mParams[i];
			}
			
			generateCheckInfo(data);
			
			return data;
		}
		
	}
	
	private static void parseMsgFromDataBytes(byte[] bytes, Msg msg){
		// cmd
		short cmd = (short) ( ((bytes[INDEX_CMD ] & 0xFF) << 8) | (bytes[INDEX_CMD + 1] & 0xFF)); 
		msg.setCmd(cmd);
		
		// length 
		short packSize = (short) (( (bytes[INDEX_SIZE] & 0xFF) << 8 | (bytes[INDEX_SIZE + 1] & 0xFF)));	

		// serial no
		long no = 0x0;
		final int noLen = 4;
		for(int i = 0; i < noLen; i++){
			no |= (long) (bytes[INDEX_NO + i] & 0xFF) << (8 * (noLen - i - 1));
		}
		msg.setNo(no);
		
		// type				
		msg.setType(bytes[INDEX_TYPE]);		
		
		// device id
		msg.setDevice(new String(bytes, INDEX_DEV, 16));
		
		// err
		msg.setSuccessful(0 == bytes[INDEX_FLAG]);
		
		// args
		short argsLen = (short) (packSize - INDEX_ARGS - 1);
//		String args = new String(bytes, INDEX_ARGS, argsLen);
		byte[] args = new byte[argsLen];
		for(int i = 0; i < argsLen; i++){
			args[i] = bytes[INDEX_ARGS + i];
		}
		msg.setArgs(args);
	}

}
