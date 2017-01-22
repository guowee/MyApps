package com.haomee.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.haomee.liulian.R;

/**
 * 内嵌ScrollView的
 */
public class PullToRefreshScrollView extends LinearLayout {

	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;

	private int mLastMotionY;

	
	private View mHeaderView;
	private View mFooterView;
	private ScrollView mScrollView;

	private int mHeaderViewHeight;
	//private int mFooterViewHeight;
	private ImageView mHeaderImageView;
	//private ImageView mFooterImageView;
	//private TextView mHeaderTextView;
	//private TextView mFooterTextView;
	//private TextView mHeaderUpdateTextView;
	//private ProgressBar mHeaderProgressBar;
	//private ProgressBar mFooterProgressBar;
	private LayoutInflater mInflater;
	
	private int mHeaderState;
	//private int mFooterState;
	private int mPullState;
	//private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	private OnFooterRefreshListener mOnFooterRefreshListener;
	private OnHeaderRefreshListener mOnHeaderRefreshListener;


	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshScrollView(Context context) {
		super(context);
		init();
	}

	AnimationDrawable animationDrawable;
	/**
	 * 
	 * 
	 * @description
	 * @param context
	 */
	private void init() {

		// Load all of the animations we need in code rather than through XML
		// 下拉时的动画
		/*mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(2000);
		mFlipAnimation.setFillAfter(true);*/
		
		
		
		// 释放时的加载动画
		mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mInflater = LayoutInflater.from(getContext());
		// header view 在此添加,保证是第一个添加到linearlayout的最上端
		try {
			addHeaderView();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addHeaderView() {
		// header view
		mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);

		mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
		/*mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
		mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
		mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);*/
		// header layout
		
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
		// 设置topMargin的值为负的header View高度,即将其隐藏在最上方
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView, 0, params);
		
		
		mHeaderImageView.setImageResource(R.anim.pull_refresh);
        animationDrawable = (AnimationDrawable) mHeaderImageView.getDrawable();
       

	}

	
	
	public View getFooterView(){
		return this.mFooterView;
	}

	public void hideFooterView() {
		if (mFooterView != null) {
			mFooterView.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// footer view 在此添加保证添加到linearlayout中的最后
		try {
			//addFooterView();
			initContentAdapterView();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @description init AdapterView like ListView,GridView and so on;or init ScrollView
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 2) {
			throw new IllegalArgumentException("this layout must contain 2 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count; ++i) {
			view = getChildAt(i);
			if (view instanceof ScrollView) {
				mScrollView = (ScrollView) view;
				
				initScrollViewListener();
			}
		}
		if (mScrollView == null) {
			throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
		}
		
		
	}
	

	private ScrollingStateListener scrollListener;

	public void setScrollingStateListener(ScrollingStateListener scrollListener){
		this.scrollListener = scrollListener;
	}

	private boolean is_finger_up;	// 手指是否放开
	private void initScrollViewListener(){
		
		mScrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					is_finger_up = false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					handler_scrolling.sendEmptyMessageDelayed(0, 20);
					is_finger_up = true;
				}
				return false;
			}
		});
	}
	
	private Handler handler_scrolling = new Handler() {
		
		private int lastScrollY = -1;
		
		public void handleMessage(android.os.Message msg) {

			int scrollY = mScrollView.getScrollY();
			
			// 如果当前的滚动位置和上次相同，表示已停止滚动
			if (scrollY == lastScrollY) {
				scrollListener.onScrollStop();		// 停止滚动
				
				if(is_finger_up){
					scrollListener.onScrollStopAndUp();		// 停止滚动并且手指放开
				}
				
				//Toast.makeText(getContext(), "停止滚动", Toast.LENGTH_SHORT).show();
				
				int scrollViewHeight = ((View)mScrollView.getParent()).getHeight();
				
				View scrollLayout = mScrollView.getChildAt(0);
				
				// 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
				// 给400高度的距离，将要到达底部
				if (scrollViewHeight + scrollY +400 >= scrollLayout.getHeight()) {
					//Toast.makeText(getContext(), "滑到了页面底部", Toast.LENGTH_SHORT).show();
					
					scrollListener.onBottom();
					
				}
				//myScrollView.checkVisibility();
				
				//Log.i("test","所在界面高度"+scrollViewHeight+"  滚动位置："+scrollY+"  总长度："+scrollLayout.getHeight());
			} else {
				lastScrollY = scrollY;
				
				// 5毫秒后再次对滚动位置进行判断
				handler_scrolling.sendEmptyMessageDelayed(0, 20);
			}
		};

	};
	
	public interface ScrollingStateListener{
		public void onScrollStop();
		public void onScrollStopAndUp();	// 停止滚动，并且手指放开
		public void onBottom();
	};
	

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {

		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:

			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;
			
			boolean isRefreshScroll = isRefreshViewScroll(deltaY);
			
			//Log.i("test", "deltaY:"+deltaY+"   isRefreshScroll:"+isRefreshScroll);
			
			if (isRefreshScroll) {
				return true;
			}
						
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/*
	 * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
	 * false)则由PullToRefreshTopic 的子View来处理;否则由下面的方法来处理(即由PullToRefreshTopic自己来处理)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int y = (int) event.getRawY();
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			
			
			if (mPullState == PULL_DOWN_STATE) {
				headerPrepareToRefresh(deltaY);
			} 
			
			/*else if (mPullState == PULL_UP_STATE) {	// 执行上拉
				footerPrepareToRefresh(deltaY);
			}*/
			
			
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();
			if (mPullState == PULL_DOWN_STATE) {
				if (topMargin >= 0) {
					// 开始刷新
					headerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			} 
			
			
			/*else if (mPullState == PULL_UP_STATE) {
				if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
					// 开始执行footer 刷新
					footerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			}*/
			
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * @description 是否应该到了父View,即PullToRefreshTopic滑动
	 * @param deltaY , deltaY > 0 是向下运动,< 0是向上运动
	 * @return
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING) {
			return false;
		}

		// 对于ScrollView
		if (mScrollView != null) {
			// 子scroll view滑动到最顶端
			//View child = mScrollView.getChildAt(0);
			if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				mPullState = PULL_DOWN_STATE;
				return true;
			} 
			/*else if (deltaY < 0  && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY() ) {
				mPullState = PULL_UP_STATE;
				return true;
			}*/

		}
		return false;
	}

	/**
	 * header 准备刷新,手指移动过程,还没有释放
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
		if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
			//mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
			//mHeaderUpdateTextView.setVisibility(View.GONE);
			//mHeaderImageView.clearAnimation();
			//mHeaderImageView.startAnimation(mFlipAnimation);
			
			 animationDrawable.start();
			
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释放
			//mHeaderImageView.clearAnimation();
			//mHeaderImageView.startAnimation(mFlipAnimation);
			
			//animationDrawable.start();
			
			//animationDrawable.stop();
			
			// mHeaderImageView.
			//mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	/**
	 * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view
	 * 高度是一样，都是通过修改header view的topmargin的值来达到
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	/*private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 如果header view topMargin 的绝对值大于或等于header + footer 的高度
		// 说明footer view 完全显示出来了，修改footer view 的提示状态
		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
			mFooterTextView.setText(R.string.pull_to_refresh_footer_release_label);
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
			mFooterState = PULL_TO_REFRESH;
		}
	}*/

	/**
	 * 修改Header view top margin的值
	 * 
	 */
	private int changingHeaderViewTopMargin(int deltaY) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.3f;
		// 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
		// 表示如果是在上拉后一段距离,然后直接下拉
		if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderViewHeight) {
			return params.topMargin;
		}
		// 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
		if (deltaY < 0 && mPullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= mHeaderViewHeight) {
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
		return params.topMargin;
	}

	/**
	 * header refreshing
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		//mHeaderImageView.setVisibility(View.GONE);
		//mHeaderImageView.clearAnimation();
		//mHeaderImageView.setImageDrawable(null);
		//mHeaderProgressBar.setVisibility(View.VISIBLE);
		//mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}

	/**
	 * footer refreshing
	 * 
	 * @author ares@haomee.net
	 * 牛逼王子太牛逼了
	 */
	/*public void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mFooterImageView.setVisibility(View.GONE);
		mFooterImageView.clearAnimation();
		mFooterImageView.setImageDrawable(null);
		mFooterProgressBar.setVisibility(View.VISIBLE);
		mFooterTextView.setText(R.string.pull_to_refresh_footer_refreshing_label);
		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onFooterRefresh(this);
		}
	}
*/
	/**
	 * 设置header view 的topMargin的值
	 * 
	 * @description
	 * @param topMargin
	 *            ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
	 * @author ares@haomee.net
	 * 牛逼王子太牛逼了
	 */
	private void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}

	/**
	 * header view 完成更新后恢复初始状态
	 * 
	 */
	public void onHeaderRefreshComplete() {
		//setHeaderTopMargin(-mHeaderViewHeight);
		//mHeaderImageView.setVisibility(View.VISIBLE);
		//mHeaderImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
		//mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
		//mHeaderProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mHeaderState = PULL_TO_REFRESH;

				animationDrawable.stop();
				
				anim_topMargin = getHeaderTopMargin();
				handler_anim.sendEmptyMessage(0);
			}
		}, 1000);
		
		
	}
	
	int anim_topMargin;
	private Handler handler_anim = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if(anim_topMargin > -mHeaderViewHeight){
				anim_topMargin-=40;
				setHeaderTopMargin(anim_topMargin);
				
				handler_anim.sendEmptyMessageDelayed(0, 60);
			}else{
				setHeaderTopMargin(-mHeaderViewHeight);
				handler_anim.removeMessages(0);
			}
			
		};
	};

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
/*	public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		//setLastUpdated(lastUpdated);
		onHeaderRefreshComplete();
	}
*/
	/**
	 * footer view 完成更新后恢复初始状态
	 */
	/*public void onFooterRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mFooterImageView.setVisibility(View.VISIBLE);
		mFooterImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow_up);
		mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
		mFooterProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mFooterState = PULL_TO_REFRESH;
	}*/

	/**
	 * Set a text to represent when the list was last updated.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
/*	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
			mHeaderUpdateTextView.setVisibility(View.GONE);
			mHeaderUpdateTextView.setText(lastUpdated);
		} else {
			mHeaderUpdateTextView.setVisibility(View.GONE);
		}
	}*/


	private int getHeaderTopMargin() {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}


	public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer
	 * view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		public void onFooterRefresh(PullToRefreshScrollView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshScrollView view);
	}
}
