package com.haomee.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;

/**
 * 自定义的ScrollView，在其中动态地对图片进行添加。
 * 
 */
public class WaterFallScrollView extends ScrollView {

	private int column_num = 2;
	private int item_topMargin;
	private int column_margin;
	
	private int[] columnHeights;
	private LinearLayout[] columnLayouts;
	
	private List<View> itemList = new ArrayList<View>();

	private LinearLayout container;


	private Context context;
	public WaterFallScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		//taskCollection = new HashSet<LoadImageTask>();
	}
	
	/**
	 * 初始化瀑布流参数，单位dp
	 * @param columnNum	列数
	 * @param columnMargin	横向间距
	 * @param itemMargin	竖向间距
	 */
	public void init(int columnNum, int columnMargin, int itemMargin){
		
		this.column_num = columnNum;
		this.item_topMargin = ViewUtil.dip2px(this.getContext(), itemMargin);
		this.column_margin = ViewUtil.dip2px(this.getContext(), columnMargin);
		
		container = (LinearLayout) this.findViewById(R.id.container);
		columnLayouts = new LinearLayout[column_num];
		columnHeights = new int[column_num];
		
		for (int i = 0; i < column_num; i++) {
			LinearLayout layout = new LinearLayout(this.getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			params.leftMargin = column_margin;
			if(i==column_num-1){
				params.rightMargin = column_margin;
			}
			
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(params);

			columnLayouts[i] = layout;
			container.addView(layout);
		}
	}
	
	// 获取每列的宽度
	public int getItemWidth(){
		return columnLayouts[0].getWidth();
	}
	
	
	// 清空所有元素
	public void clearItems(){
		
		itemList.clear();
		
		for(int i=0;i<columnLayouts.length;i++){
			columnLayouts[i].removeAllViews();
			columnHeights[i] = 0;
		}
	}


	
	public int getFirstVisibleItem(){
		int index = 0;
		for(index=0;index<itemList.size();index++){
			View item = itemList.get(index);
			int bottom = item.getBottom();
			
			if(bottom>this.getScrollY()-container.getTop()){
				break;
			}
		}
		return index;
	}
	
	public int getLastVisibleItem(){
		int index = 0;
		for(index=0;index<itemList.size();index++){
			View item = itemList.get(index);
			int top = item.getTop();
			
			if(top-this.getHeight()>this.getScrollY()-container.getTop()){				
				break;
			}
		}
		
		// 刚好滑到底部的时候
		if(index==0 && itemList.size()>0){
			index = itemList.size()-1;
		}
		
		return index;
	}
	
	
	public void loadImage(int first, int last){
		
		long time_start = System.currentTimeMillis();

		if(first<0){
			first = 0;
		}
		if(last>itemList.size()-1){
			last = itemList.size()-1;
		}
		
		//Log.i("test", "加载图片："+first+"-"+last);
		
		int first_recycle = first-10;	// 远处的图片
		int last_recycle = last+10;
		
		int count_recycle=0, count_loading=0;


		for(int index=0;index<itemList.size();index++){
			View item = itemList.get(index);
			final ImageView imageView = (ImageView) item.getTag(R.id.imageView);
			boolean is_img_loaded = (Boolean) item.getTag(R.id.is_img_loaded);
			
			if(index>=first && index<=last){	// 可见元素
				
				//Log.i("test","可见："+index);
				
				if(imageView!=null && !is_img_loaded){
					String url = (String) item.getTag(R.id.imgUrl);					
					ImageLoaderCharles.getInstance(context).addTask(url, imageView);
					item.setTag(R.id.is_img_loaded, true);
					
					count_loading++;
				}
			}else if(index<first_recycle || index>last_recycle){		// 太远的回收掉
				if(imageView!=null && is_img_loaded){
					// 恢复为默认图
					imageView.setImageResource(R.drawable.item_default);
					//imageView.setImageDrawable(null);


					item.setTag(R.id.is_img_loaded, false);

					count_recycle++;
				}
			}
		}
		

		//System.gc(); // 提醒系统回收垃圾，只是提醒而已
		
		long time_end = System.currentTimeMillis();
		//Log.i("test", "加载："+count_loading+"释放："+count_recycle+"  耗时："+(time_end-time_start));
	}
	
	
	
	
	public void loadVisibleImage(){
		
		
		int first = getFirstVisibleItem()-10;		// 前后预留10个
		int last = getLastVisibleItem()+10;
		
		loadImage(first, last);
		
	}


	
	
	public void addItem(View item) {
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = item_topMargin;
		/*params.leftMargin = 20;
		params.rightMargin = 20;*/
		
		int item_height = (Integer) item.getTag(R.id.itemHeight);
		LinearLayout container = findColumnToAdd(item_height);
		container.addView(item, params);
		itemList.add(item);
	}

	/**
	 * 找到此时应该添加图片的一列。原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列。
	 * 
	 * @param imageView
	 * @param imageHeight
	 * @return 应该添加图片的一列
	 */
	private LinearLayout findColumnToAdd(int item_height) {
	
		int min_index=0;
		int min_height = columnHeights[0];
		for (int i = 1; i < columnLayouts.length; i++) {
			int columnHeight = columnHeights[i];
			if (columnHeight < min_height) {
				min_height = columnHeight;
				min_index = i;
			}
		}
		columnHeights[min_index]+=item_height;
		

		return columnLayouts[min_index];
	}

}