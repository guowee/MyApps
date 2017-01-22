package com.hipad.smarthome.chart;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.view.View;

public class TemperatureAnalysisChart extends Chart {

	private double xThreshold = 9;
	private double yThreshold = 30;
	private int minUnit = 5;
	
	
	public TemperatureAnalysisChart(Context context) {
		super(context);
	}

	@Override
	public View getView(String[] xData, double[] yData, String xTitle, String yTitle, String chartTitle) {
		
		super.xDataTitles = xData;
		super.yDataValues = yData;
		checkLimitedValue();
		return getBarChart(xTitle, yTitle, chartTitle);
	}

	private View getBarChart(String xTitle, String yTitle, String chartTitle) {
		XYSeries series = new XYSeries(chartTitle);
		XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
		Dataset.addSeries(series);

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer _renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(_renderer);

		 mRenderer.setApplyBackgroundColor(true); //设定背景颜色
	    mRenderer.setBackgroundColor(Color.LTGRAY); //设定图内围背景颜色
	    
	    mRenderer.setAxisTitleTextSize(24);
	    
		// 设置图表的外边框(上,左,下,右)
		mRenderer.setMargins(new int[] { 40, 50, 50, 20 });
		
		mRenderer.setXLabelsPadding(30);
		
		mRenderer.setMarginsColor(Color.WHITE); // 设定图外围背景颜色
		mRenderer.setTextTypeface(null, Typeface.BOLD); // 设定文字style

		mRenderer.setShowGrid(true); // 设定网格
		mRenderer.setGridColor(Color.GRAY); // 设定网格颜色

		//mRenderer.setChartTitle(chartTitle); // 设定标头文字
		mRenderer.setXTitle(xTitle); // X轴名称
		mRenderer.setYTitle(yTitle); // Y轴名称
		mRenderer.setLabelsColor(Color.BLACK); // 设定标头文字颜色
		mRenderer.setChartTitleTextSize(20); // 设定标头文字大小
		mRenderer.setAxesColor(Color.BLACK); // 设定双轴颜色
		mRenderer.setBarSpacing(0.5); // 设定bar间的距离

		// Renderer.setXTitle(XTitle); //设定X轴文字
		// Renderer.setYTitle(YTitle); //设定Y轴文字
		mRenderer.setXLabelsColor(Color.BLACK); // 设定X轴文字颜色
		mRenderer.setYLabelsColor(0, Color.BLACK); // 设定Y轴文字颜色
		mRenderer.setXLabelsAlign(Align.CENTER); // 设定X轴文字置中
		//mRenderer.setYLabelsAlign(Align.CENTER); // 设定Y轴文字置中
		mRenderer.setXLabelsAngle(-25); // 设定X轴文字倾斜度

		mRenderer.setZoomEnabled(false, false);
		mRenderer.setPanEnabled(false, false);
		
		mRenderer.setYLabels(12);
		
		// 设置轴刻度文字的大小
		mRenderer.setLabelsTextSize(18);
		// 设置图例文字大小
		mRenderer.setLegendTextSize(25);
		
		// 设定X轴不显示数字, 改以程式设定文字
		mRenderer.setXLabels(0);
		
		// 设定X轴最小,大值
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(xThreshold);
		
		// 设定Y轴最小,大值
		mRenderer.setYAxisMin(0);
		mRenderer.setYAxisMax(getMaxYValue()>yThreshold?checkAxisMax(minUnit,getMaxYValue()):yThreshold);
		
		_renderer.setColor(Color.RED); // 设定Series颜色
		// yRenderer.setDisplayChartValues(true); //展现Series数值

		series.add(0, 0);
		mRenderer.addXTextLabel(0, "");
		for (int r = 0; r < yDataValues.length; r++) {
			mRenderer.addXTextLabel(r + 1, xDataTitles[r]);
			series.add(r + 1, yDataValues[r]);
		}
		//series.add(11, 0);
		//mRenderer.addXTextLabel(yData.length + 1, "");

		View view = ChartFactory.getBarChartView(context, Dataset, mRenderer, Type.DEFAULT);
		return view;
	}
}