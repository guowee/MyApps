package com.haomee.view;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.haomee.liulian.R;

public class LoadingDialog extends Dialog{
	private static boolean flag=false;
	private ImageView dialog_img;
	//private String text;
	private Animation animation;

	public LoadingDialog(Context context) {
		super(context, R.style.loading_dialog);		// 自定义样式
	}
	
    public LoadingDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {  
        super(context, cancelable, cancelListener);  
    } 
    
    public void setFlag(boolean flag){
    	this.flag=flag;
    }
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        if(flag){
        	this.setContentView(R.layout.layout_loading_1);
        }else {
        	this.setContentView(R.layout.layout_loading);
        }
        
        //LayoutInflater inflater = LayoutInflater.from(getContext());
		//View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
		//LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		dialog_img = (ImageView) this.findViewById(R.id.icon_loading);
		
		//animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_loading);
		
		// 加载动画
		animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1000);
		animation.setRepeatCount(-1);
		animation.setInterpolator(new LinearInterpolator());
		
		this.setCanceledOnTouchOutside(false);	// 点击不消失
		
		/*TextView dialog_txt = (TextView) this.findViewById(R.id.dialog_txt);// 提示文字
		if(text!=null){
			dialog_txt.setText(text);
		}*/
		

		//loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		/*this.setCancelable(false);// 不可以用“返回键”取消
		this.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
          
        setContentView(view);  */
    }
    
	// 设置加载信息
    /*public void setDialogText(String text){
    	this.text = text;
    }*/
    
    @Override
    public void show() {
    	try{
    		super.show();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	dialog_img.startAnimation(animation);
    }
    
    @Override
    public void cancel() {
    	super.cancel();
    	if(animation!=null){
    		animation.cancel();
    	}
    	
    }
    
    @Override
    public void dismiss() {
    	
    	// Activity被销毁了，再调用dismiss就会报错，不处理
    	try{
    		super.dismiss();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }

}