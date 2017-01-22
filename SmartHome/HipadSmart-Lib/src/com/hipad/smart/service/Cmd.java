/**
 * Cmd.java 2014-11-18
 */
package com.hipad.smart.service;

import com.hipad.smart.util.MsgBodyData;

/**
 * An abstract class, represents the cmd sent to the target device. 
 * @author wangbaoming
 *
 */
public abstract class Cmd {
	/**
	 * 
	 * @return
	 * 		cmd body
	 */
	abstract public MsgBodyData getBody();
	/**
	 * 
	 * @return
	 * 		cmd flag
	 */
	abstract public byte getFlag();
	/**
	 * 
	 * @return
	 * 		cmd number
	 */
	abstract public byte getCmdNo();
}
