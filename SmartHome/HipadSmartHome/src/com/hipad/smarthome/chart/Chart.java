package com.hipad.smarthome.chart;

import android.content.Context;
import android.view.View;

public abstract class Chart {

	String[] xDataTitles;
	double[] yDataValues;
	double maxYValue, minYValue;
	String maxXTitle, minXTitle;
	Context context;

	public Chart(Context context) {
		this.context = context;
	}

	public double getMaxYValue() {
		return maxYValue;
	}

	public double getMinYValue() {
		return minYValue;
	}

	public String getMaxXTitle() {
		return maxXTitle;
	}

	public String getMinXTitle() {
		return minXTitle;
	}

	public void checkLimitedValue() {
		Object[] resMax = checkMaximum(yDataValues);
		Object[] resMin = checkMinimum(yDataValues);
		if ((Integer) resMax[0] != -1) {
			maxYValue = (Double) resMax[1];
			maxXTitle = xDataTitles[(Integer) resMax[0]];
		}
		if ((Integer) resMin[0] != -1) {
			minYValue = (Double) resMin[1];
			minXTitle = xDataTitles[(Integer) resMin[0]];
		}
	}

	public Object[] checkMaximum(double[] data) {
		int location = -1;
		double max = -1;
		if (data != null && data.length != 0) {
			max = data[0];
			location = 0;
			for (int i = 1; i < data.length; i++)
				if (data[i] > max) {
					location = i;
					max = data[i];
				}
		}
		return new Object[] { location, max };
	}

	public Object[] checkMinimum(double[] data) {
		int location = -1;
		double max = -1;
		if (data != null && data.length != 0) {
			max = data[0];
			location = 0;
			for (int i = 1; i < data.length; i++)
				if (data[i] < max) {
					location = i;
					max = data[i];
				}
		}
		return new Object[] { location, max };
	}

	public double checkAxisMax(int minUnit, double value) {
		return (value % minUnit) == 0 ? value : value + minUnit
				- (value % minUnit);
	}

	public abstract View getView(String[] xData, double[] yData, String xTitle,
			String yTitle, String chartTitle);
}