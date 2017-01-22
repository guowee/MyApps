/**
 * MsgUtil.java 2014骞�10鏈�5鏃�
 */
package com.hipad.smart.local.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangbaoming
 *
 */
public class MsgUtil {

	public static Map<String, Object> parseArgs(String args){
		HashMap<String , Object> mapArgs = new HashMap<String, Object>();
		if(null == args) return mapArgs;
		String[] arrArgs = args.split(",");
		for (String arg : arrArgs) {
			if(arg.contains("=")){
				int equalIndex = arg.indexOf("=");
				String key = arg.substring(0, equalIndex);
				String value = arg.substring(equalIndex + 1);
				mapArgs.put(key, value);
			}
		}	
		
		return mapArgs;
	}
}
