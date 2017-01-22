/**
 * Protocol.java 2014-9-28
 */
package com.hipad.smart.local.msg;

import java.net.DatagramPacket;

/**
 * @author wangbaoming
 *
 */
abstract class Protocol implements Parser{
	private String mName = "UNKNOWN";	
	
	static abstract class Builder{
		public abstract byte[] build(); // for tcp
		public abstract DatagramPacket build(String ip, int port); // for udp
	}
}

interface Parser {
	public Msg parseDataPack(DatagramPacket pack); // for udp
	public Msg parseDataPack(byte[] data, String ip); // for tcp		

	public byte[] createDataPack(Msg msg); // for tcp
	public DatagramPacket createDataPack(Msg msg, int port); // for udp
}