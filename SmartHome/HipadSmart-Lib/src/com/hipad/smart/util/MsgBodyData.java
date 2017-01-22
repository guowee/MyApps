/**
 * MsgBodyData.java 2014-11-10
 */
package com.hipad.smart.util;

import android.util.Base64;

/**
 * present the message body, which holds the binary data.
 * @author wangbaoming
 *
 */
public abstract class MsgBodyData {
	private byte[] mData;
	
	protected MsgBodyData(int size) {
		mData = new byte[size];
	}
	
	protected MsgBodyData(String dataBase64) {
		decode(dataBase64);
	}
	
	protected MsgBodyData(byte[] data) {
		mData = data;
	}
	/**
	 * get the binary data.
	 * @return
	 * 		the bytes
	 */
	public byte[] getData(){
		return mData;
	}
	
	/**
	 * set the byte at the index.
	 * @param index
	 * 		the index of the byte to be set
	 * @param data
	 * 		the byte data
	 */
	protected void setByte(int index, byte data){
		mData[index] = data;
	}
	
	/**
	 * get the byte data at index position.
	 * @param index
	 * 		the index of the data
	 * @return
	 * 		the byte data
	 */
	protected byte getByte(int index){
		return mData[index];
	}
	
	/**
	 * set the bit in the byte at the index.
	 * @param index
	 * 		the byte index
	 * @param bit
	 * 		the bit index in the byte at the index
	 * @param set
	 * 		if true, set the bit; clean the bit otherwise 
	 */
	protected void setBit(int index, int bit, boolean set){
		byte data = mData[index];
		if(set){ // set the bit
			data |= (byte) (1 << bit);
		}else{ // clean the bit
			data &= (byte) ((1 << 8) - 1) ^ (1 << bit);			
		}
				
		mData[index] = data;
	}
	
	/**
	 * get the bit in the byte at the index
	 * @param index
	 * 		the byte index
	 * @param bit
	 * 		the bit index int the byte at the index
	 * @return
	 * 		if the bit is set, true, false otherwise
	 */
	protected boolean getBit(int index, int bit){
		byte data = mData[index];
		return 0 != (data & (1 << bit));
	}
	
	/**
	 * encode the body data in base64.
	 * @return
	 * 		the body encoded in base64
	 */
	public String encode(){		
		String dataEncoded = Base64.encodeToString(mData, Base64.DEFAULT);
		System.out.println("encode: " + dataEncoded);
		byte[] dataDecoded = Base64.decode(dataEncoded, Base64.DEFAULT);
		
		// TODO cut the "=="
		return Base64.encodeToString(mData, Base64.DEFAULT);
	}
	
	private void decode(String body){
		System.out.println("decode: " + body);
		
		mData = Base64.decode(body, Base64.DEFAULT);
	}
}
