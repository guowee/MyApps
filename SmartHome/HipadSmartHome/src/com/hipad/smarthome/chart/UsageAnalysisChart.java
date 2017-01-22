package com.hipad.smarthome.chart;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.view.View;

public class UsageAnalysisChart extends Chart{

	private double yThreshold = 30;
	private int minUnit = 5;
	
	public UsageAnalysisChart(Context context) {
		super(context);
	}

	@Override
	public View getView(String[] xData, double[] yData, String xTitle, String yTitle, String chartTitle) {
		
		super.xDataTitles = xData;
		super.yDataValues = yData;
		checkLimitedValue();

		// X,Y坐标值输入
		XYMultipleSeriesDataset dataset = buildDataset(chartTitle);
		XYMultipleSeriesRenderer renderer = buildRenderer();
		yThreshold = getMaxYValue()>yThreshold?checkAxisMax(minUnit,getMaxYValue()):yThreshold;
		setChartSettings(renderer, xTitle, yTitle, 0, 0, 0, yThreshold, Color.BLACK);
		return ChartFactory.getLineChartView(context, dataset, renderer);
	}

	// 定义折线图
	protected void setChartSettings(XYMultipleSeriesRenderer mRenderer,
			String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor) {

		/**
		 * Chart define
		 */
		mRenderer.setXTitle(xTitle); // X轴名称
		mRenderer.setYTitle(yTitle); // Y轴名称
		mRenderer.setXAxisMin(xMin); // X轴显示最小值
		//mRenderer.setXAxisMax(xMax); // X轴显示最大值
		mRenderer.setYAxisMin(yMin); // Y轴显示最小值
		mRenderer.setYAxisMax(yMax); // Y轴显示最大值

		mRenderer.setAntialiasing(true);
		// mRenderer.setChartTitle(title);// 折线图名称
		mRenderer.setPanEnabled(false, false);// 是否允许拖动X轴Y轴

		// 是否显示网格
		mRenderer.setShowGridX(true);
		mRenderer.setShowGridY(false);

		mRenderer.setShowLegend(true);
		//mRenderer.setXLabels(20);
		mRenderer.setYLabels(12);// 设置y轴显示12个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		mRenderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮

		mRenderer.setZoomEnabled(false, false);
		/**
		 * Layout define
		 */
		//mRenderer.setMarginsColor(Color.rgb(0xDE, 0xFF, 0xF9));// 设定背景颜色
		mRenderer.setMarginsColor(Color.WHITE);

		mRenderer.setAxisTitleTextSize(24);
		
		// 设置图表的外边框(上,左,下,右)
		mRenderer.setMargins(new int[] { 40, 50, 50, 20 });
		mRenderer.setXLabelsPadding(30);
		mRenderer.setXLabels(0);
		
		// 设置整个图表标题文字的大小
		mRenderer.setChartTitleTextSize(40);

		// XY轴内的颜色
		mRenderer.setBackgroundColor(Color.rgb(0xEF, 0xFF, 0xFC));

		// 格线颜色
		mRenderer.setGridColor(Color.GRAY);

		// 设定标籤颜色
		mRenderer.setLabelsColor(Color.BLACK);

		// 设定坐标轴颜色
		mRenderer.setAxesColor(Color.BLACK);
		// mRenderer.setAxesColor(axesColor);

		// 设置轴标题文字的大小
		//mRenderer.setAxisTitleTextSize(24);
		
		// 设定文字style
		mRenderer.setTextTypeface(null, Typeface.BOLD);

		// 是否可改变背景颜色
		mRenderer.setApplyBackgroundColor(true);

		// X轴底字颜色
		mRenderer.setXLabelsColor(Color.BLACK);

		// Y轴线颜色
		mRenderer.setYLabelsColor(0, Color.BLACK);

		// 设置轴刻度文字的大小
		mRenderer.setLabelsTextSize(18);

		// 设置图例文字大小
		mRenderer.setLegendTextSize(25);

		// 刻度线与刻度标注之间的相对位置关系
		mRenderer.setXLabelsAlign(Align.CENTER);

		// 刻度线与刻度标注之间的相对位置关系
		mRenderer.setYLabelsAlign(Align.RIGHT);

		// X轴Text倾斜角度
		mRenderer.setXLabelsAngle(-30);
	}

	// 定义折线图的格式
	private XYMultipleSeriesRenderer buildRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();

		seriesRenderer.setLineWidth(5);
		seriesRenderer.setStroke(BasicStroke.SOLID);
		seriesRenderer.setGradientEnabled(true);

		//X轴Text
		for(int i=1;i<xDataTitles.length;i++)
			renderer.addXTextLabel(i, xDataTitles[i]);
		
		// 折线的颜色
		seriesRenderer.setColor(Color.BLUE);
		// seriesRenderer.setColor(Color.parseColor("#EEB422"));

		// 折线点的形状
		seriesRenderer.setPointStyle(PointStyle.DIAMOND);

		// 是否实心
		seriesRenderer.setFillPoints(true);

		// 将座标变成线加入图中显示
		renderer.addSeriesRenderer(seriesRenderer);

		return renderer;
	}

	private XYMultipleSeriesDataset buildDataset(String title) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries(title); // 依据每条线的名称新增

		for (int k = 1; k < xDataTitles.length; k++)
			series.add(k, yDataValues[k]);
		dataset.addSeries(series);

		return dataset;
	}
}