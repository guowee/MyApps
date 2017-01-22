package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StepResponse {
	@Expose
	@SerializedName("stepType")
	private String stepType;
	@Expose
	@SerializedName("step")
	private String step;
	@Expose
	@SerializedName("distanceTotal")
	private double distanceTotal;
	@Expose
	@SerializedName("distance")
	private String distance;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	
	public String getStepType(){
		return stepType;
	}
	public String getStep() {
		return step;
	}
	public double getDistanceTotal(){
		return distanceTotal;
	} 
	public String getDistance(){
		return distance;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		
	

}
