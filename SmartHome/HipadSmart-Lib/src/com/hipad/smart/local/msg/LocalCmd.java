/**
 * Cmd.java 2014-10-9
 */
package com.hipad.smart.local.msg;

import java.util.Date;

/**
 * @author wangbaoming
 *
 */
public class LocalCmd extends Msg {
	private static long sIndex = 0;

	public LocalCmd() {	
		setNo(generateNo());
	}
	
	public LocalCmd(int cmd, String deviceId, boolean successful, String args) {
		super(Msg.Type.CMD, cmd, deviceId, successful, args);
		setNo(generateNo());
	}
	
	private synchronized static long generateNo(){
		// increase the index
		synchronized (LocalCmd.class) {
			sIndex++;
		}
		
		long no = -1;
		// serial NO.
		Date date = new Date();
		
		no = 0x0;
		no |= (long) ((date.getYear() % 100) & 0xFF) << (8 * 7);
		no |= (long) (date.getMonth() & 0xFF) << (8 * 6);
		no |= (long) (date.getDate() & 0xFF) << (8 * 5);
		no |= (long) (date.getHours() & 0xFF) << (8 * 4);
		no |= (long) (date.getMinutes() & 0xFF) << (8 * 3);
		no |= (long) (date.getSeconds() & 0xFF) << (8 * 2);	
		
		no |= (long) (sIndex >> 8 & 0xFF) << (8 * 1);
		no |= (long) (sIndex & 0xFF) << (8 * 0);
		
		return no & 0x00000000FFFFFFFFL; // lower 4 bytes
	}
}
