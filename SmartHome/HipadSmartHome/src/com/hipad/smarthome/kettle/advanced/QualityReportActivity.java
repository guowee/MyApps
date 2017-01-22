package com.hipad.smarthome.kettle.advanced;

import java.io.File;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;
import com.hipad.smarthome.kettle.dao.WaterLevelDao;
import com.hipad.smarthome.kettle.statistics.entity.WaterLevel;

/**
 * 用水质量报告
 * 
 * @author guowei
 *
 */

public class QualityReportActivity extends BaseActivity implements IFunction,
		OnClickListener {
	public static final String MYACTION = "QualityReportActivity_action";
	private LinearLayout chartLayout;
	private ImageButton backBtn;
	private TextView title;
	private TextView notifyTxt;
	private Device localDevice = null;

	private CommonDevice cloudDevice = null;
	private DeviceController controller;
	private Context mContext;

	List<WaterLevel> datas = null;

	String deviceId = null;

	WaterLevelDao levelDao = null;
	double avg = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.quality_reporter_layout);
		mContext = this;
		levelDao = new WaterLevelDao(mContext);
		controller = new DeviceController();
		initViews();

	}

	private void initViews() {

		if (getIntent().getParcelableExtra("device") instanceof Device) {
			localDevice = getIntent().getParcelableExtra("device");

			deviceId = localDevice.getId();
			controller.init(localDevice);
		} else {

			cloudDevice = getIntent().getParcelableExtra("device");
			deviceId = cloudDevice.getDeviceId();
		}

		String titleStr = getIntent().getStringExtra("title");
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(this);

		notifyTxt = (TextView) findViewById(R.id.notify_text);
		title = (TextView) findViewById(R.id.titleTxt);
		title.setText(titleStr);
		chartLayout = (LinearLayout) findViewById(R.id.chart_layout);

		chartLayout.addView(getView(mContext));

		showTips(avg);
	}

	private void showTips(double avg) {

		if (0 <= avg && avg <= 200) {

			notifyTxt.setText(R.string.waterlevel_excellent);

		} else if (avg < 500) {
			notifyTxt.setText(R.string.waterlevel_good);
		} else if (avg < 1000) {
			notifyTxt.setText(R.string.waterlevel_preferably);
		} else {
			notifyTxt.setText(R.string.waterlevel_bad);
		}

	}

	private GraphicalView getView(Context context) {

		// query data from database
		double[] xData = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19, 20 };

		datas = levelDao.obtainWaterLevelList(MyApplication.user.getUserId(),
				deviceId);
		double[] yData = new double[20];
		double sum = 0, max = 0;
		for (int i = 0; i < datas.size(); i++) {

			yData[i] = datas.get(i).getLevel();
			if (max < yData[i]) {
				max = yData[i];
			}
			sum = sum + yData[i];
		}
		if (datas.size() == 0) {
			avg = 0;
		} else {
			avg = sum / datas.size();// 获取平均值
		}

		double[] _yData = new double[20];
		for (int i = 0; i < _yData.length; i++) {
			_yData[i] = avg;
		}

		List<double[]> xValues = new ArrayList<double[]>();
		xValues.add(xData);
		xValues.add(xData);
		List<double[]> yValues = new ArrayList<double[]>();
		yValues.add(yData);
		yValues.add(_yData);

		// 定义单位名称
		String[] unitName = new String[] { "水质", "平均值" };
		String title = "用水水质分析";
		String xTilte = "用水次数";
		String yTitle = "水质TDS值";

		int[] colors = new int[] { Color.BLUE, setLineColor(avg) };
		PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND,
				PointStyle.POINT };
		// X,Y坐标值输入
		XYMultipleSeriesDataset dataset = buildDataset(unitName, xValues,
				yValues);
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, title, xTilte, yTitle, 0, 20, 0,
				max > 0 ? max + 50 : 100, Color.BLACK);

		return ChartFactory.getLineChartView(context, dataset, renderer);
	}

	protected void setChartSettings(XYMultipleSeriesRenderer mRenderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor) {

		mRenderer.setXTitle(xTitle); // X轴名称
		mRenderer.setYTitle(yTitle); // Y轴名称
		mRenderer.setXAxisMin(xMin); // X轴显示最小值
		mRenderer.setXAxisMax(xMax); // X轴显示最大值
		mRenderer.setYAxisMin(yMin); // Y轴显示最小值
		mRenderer.setYAxisMax(yMax); // Y轴显示最大值

		mRenderer.setAntialiasing(true);
		// mRenderer.setChartTitle(title);// 折线图名称
		mRenderer.setPanEnabled(false, false);// 是否允许拖动X轴Y轴
		// 是否显示网格
		mRenderer.setShowGridX(true);
		mRenderer.setShowGridY(false);
		mRenderer.setShowLegend(true);
		mRenderer.setXLabels(20);
		mRenderer.setYLabels(15);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		mRenderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		mRenderer.setZoomEnabled(false, false);
		/**
		 * Layout define
		 */
		// mRenderer.setMarginsColor(Color.rgb(0xDE, 0xFF, 0xF9));// 设定背景颜色
		mRenderer.setMarginsColor(Color.WHITE);

		// 设置图表的外边框(上,左,下,右)
		mRenderer.setMargins(new int[] { 40, 50, 50, 20 });

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
		mRenderer.setAxisTitleTextSize(24);

		// 是否可改变背景颜色
		mRenderer.setApplyBackgroundColor(true);

		// X轴底字颜色
		mRenderer.setXLabelsColor(Color.BLACK);

		// Y轴线颜色
		mRenderer.setYLabelsColor(0, Color.BLACK);

		// 设置轴刻度文字的大小
		mRenderer.setLabelsTextSize(18);

		// 设置图例文字大小(收缩压 …)
		mRenderer.setLegendTextSize(25);

		// 刻度线与刻度标注之间的相对位置关系
		mRenderer.setXLabelsAlign(Align.CENTER);

		// 刻度线与刻度标注之间的相对位置关系
		mRenderer.setYLabelsAlign(Align.RIGHT);

		// X轴Text倾斜角度
		mRenderer.setXLabelsAngle(0);

	}

	/**
	 * 曲线图(数据集) : 创建曲线图图表数据集
	 * 
	 * @param 赋予的标题
	 * @param xValues
	 *            x轴的数据
	 * @param yValues
	 *            y轴的数据
	 * @return XY轴数据集
	 */

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {

		// 数据集
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		addXYSeries(mDataset, titles, xValues, yValues, 0);
		return mDataset;

	}

	/**
	 * 曲线图(被调用方法) : 添加 XY 轴坐标数据 到 XYMultipleSeriesDataset 数据集中
	 * 
	 * @param dataset
	 *            最后的 XY 数据集结果, 相当与返回值在参数中
	 * @param titles
	 *            要赋予的标题
	 * @param xValues
	 *            x轴数据集合
	 * @param yValues
	 *            y轴数据集合
	 * @param scale
	 *            缩放
	 * 
	 *            titles 数组个数 与 xValues, yValues 个数相同 tittle 与 一个图标可能有多条曲线,
	 *            每个曲线都有一个标题 XYSeries 是曲线图中的 一条曲线, 其中封装了 曲线名称, X轴和Y轴数据
	 */
	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {

		int length = titles.length; /* 获取标题个数 */
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale); /* 单条曲线数据 */
			double[] xV = xValues.get(i); /* 获取该条曲线的x轴坐标数组 */
			double[] yV = yValues.get(i); /* 获取该条曲线的y轴坐标数组 */
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]); /* 将该条曲线的 x,y 轴数组存放到 单条曲线数据中 */
			}
			dataset.addSeries(series); /* 将单条曲线数据存放到 图表数据集中 */

		}

	}

	/**
	 * 曲线图(渲染器) : 创建曲线图图表渲染器
	 * 
	 * @param 每条曲线要渲染的颜色
	 *            , 把这些颜色放入数组
	 * @param 每条曲线绘制点的风格
	 * @return 数据渲染器集合
	 */

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();/* 创建曲线图图表渲染器 */

		setRenderer(renderer, colors, styles);
		return renderer;

	}

	/**
	 * 曲线图(渲染器 - 被调用方法) : 设置坐标轴渲染器
	 * 
	 * @param renderer
	 *            设置的渲染器集合, 这个参数相当与返回值, 设置渲染器结果保存在这个参数中
	 * @param colors
	 *            要渲染的颜色集合
	 * @param styles
	 *            要渲染的样式集合
	 */
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {

		renderer.setAxisTitleTextSize(16); /* 设置XY轴标题字体大小 */
		renderer.setChartTitleTextSize(20); /* 设置表格标题文字大小 */
		renderer.setLabelsTextSize(15); /* 设置标签文字大小 */
		renderer.setLegendTextSize(15); /* 设置说明文字大小 */
		renderer.setPointSize(5f); /* 设置点的大小 */
		renderer.setMargins(new int[] { 20, 30, 15, 20 }); /* 设置 margin, 单位像素 */
		int length = colors.length; /* 获取渲染器的个数, 即有多少条曲线 */
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer(); /* 单个曲线的渲染器 */
			r.setColor(colors[i]); /* 为单个曲线渲染器设置曲线颜色 */
			r.setPointStyle(styles[i]); /* 为单个曲线渲染器设置曲线风格 */

			r.setLineWidth(5);
			r.setStroke(BasicStroke.SOLID);
			r.setGradientEnabled(true);
			// 是否实心
			r.setFillPoints(true);
			renderer.addSeriesRenderer(r); /* 将单个曲线渲染器设置到渲染器集合中 */
		}
	}

	@Override
	public String getName() {
		return "用水质量报告";
	}

	@Override
	public Intent execute(Context context) {
		Intent intent = new Intent();
		intent.setAction(MYACTION);
		return intent;
	}

	@Override
	public boolean isForResult() {
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		default:
			break;
		}

	}

	private int setLineColor(double tds) {
		int color = Color.rgb(111, 203, 125);
		if (tds * 10 <= 200) {
			color = Color.rgb(111, 203, 125);
		} else if (tds * 10 <= 500) {
			color = Color.rgb(37, 183, 188);
		} else if (tds * 10 <= 1000) {
			color = Color.rgb(255, 188, 0);
		} else {
			color = Color.rgb(229, 0, 17);
		}
		return color;
	}

}
