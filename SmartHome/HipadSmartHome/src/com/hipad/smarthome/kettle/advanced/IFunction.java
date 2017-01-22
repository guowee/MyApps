package com.hipad.smarthome.kettle.advanced;

import android.content.Context;
import android.content.Intent;

public interface IFunction {
	/** A constant for the name field in a list activity. */
	String NAME = "name";

	/**
	 * Returns the function name.
	 * 
	 * @return the function name
	 */
	public String getName();

	/**
	 * Executes the function demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context);
	
	
	
	public boolean isForResult();

}