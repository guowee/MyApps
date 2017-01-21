package com.example.settings;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
@SuppressWarnings("deprecation")
public class SecondLevelActivity extends ChoicePrefsActivity{
	
	CheckBoxPreference android,ios,wp;
	
	//CheckBoxPreference java,c,js;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.bgColor);
		setContentView(R.layout.prefs_list_content);
		addPreferencesFromResource(R.xml.second_level_prefs);
		
		initPreference();
		
		//添加到单选列表中，因为这里有两个单选列表，所以就不返回选择的值给前一个界面了。
		addToSingleChoiceList(android,ios,wp);
		//如果再放入一组，那么就变成了两组单选列表了
		//addToSingleChoiceList(java,c,js);
			
	}
	private void initPreference() {
		android = (CheckBoxPreference) findPreference("android_prefs");
		ios = (CheckBoxPreference) findPreference("ios_prefs");
		wp = (CheckBoxPreference) findPreference("wp_prefs");
		
		/*java = (CheckBoxPreference) findPreference("java_prefs");
		c = (CheckBoxPreference) findPreference("c_prefs");
		js = (CheckBoxPreference) findPreference("js_prefs");*/
	}

}
