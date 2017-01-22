package com.hipad.smarthome.adapter;

import java.util.ArrayList;
import com.hipad.smarthome.utils.HZPinyinHelper;
import android.content.Context;

/**
 * @author EthanChung
 */
public enum Scene {
	UserDefine(new byte[]{0x00,0x00},0,0),
	
	KeepWarmDefine(new byte[]{0x00,0x01},0,0),
	BoilDefine(new byte[]{0x00,0x02},0,0),
	
	SoybeanMilk(new byte[]{0x01,0x00},0,0),
	Milk(new byte[]{0x02,0x00},0,0),
	Coffee(new byte[]{0x03,0x00},0,0),
	SugerTea (new byte[]{0x04,0x00}, 38,0),
	GreenTea(new byte[]{0x05,0x00},0,0),
	BlackTea(new byte[]{0x06,0x00},0,0),
	YellowTea(new byte[]{0x07,0x00},0,0),
	OolongTea(new byte[]{0x08,0x00},0,0),
	PuerTea(new byte[]{0x09,0x00},0,0),
	WhiteTea(new byte[]{0x0A,0x00},0,0),
	DarkTea(new byte[]{0x0B,0x00},0,0),
	ScentedTea(new byte[]{0x0C,0x00},0,0),

	SoybeanMilk01(new byte[]{0X01,0x01},80,0),
	SoybeanMilk02(new byte[]{0X01,0x02},70,0),
	SoybeanMilk03(new byte[]{0X01,0x03},73,0),
	SoybeanMilk04(new byte[]{0X01,0x04},85,0),
	SoybeanMilk05(new byte[]{0X01,0x05},75,0),
	SoybeanMilk06(new byte[]{0X01,0x06},72,0),
	
	Milk01(new byte[]{0x02,0x01},55,0),
	Milk02(new byte[]{0x02,0x02},40,0),
	Milk03(new byte[]{0x02,0x03},40,0),
	Milk04(new byte[]{0x02,0x04},40,0),
	Milk05(new byte[]{0x02,0x05},40,0),
	Milk06(new byte[]{0x02,0x06},60,0),
	Milk07(new byte[]{0x02,0x07},45,0),
	Milk08(new byte[]{0x02,0x08},37,0),
	Milk09(new byte[]{0x02,0x09},37,0),
	Milk10(new byte[]{0x02,0x0A},37,0),
	Milk11(new byte[]{0x02,0x0B},50,0),
	Milk12(new byte[]{0x02,0x0C},45,0),
	Milk13(new byte[]{0x02,0x0D},50,0),
	Milk14(new byte[]{0x02,0x0E},45,0),
	Milk15(new byte[]{0x02,0x0F},40,0),
	Milk16(new byte[]{0x02,0x10},50,0),
	Milk17(new byte[]{0x02,0x11},50,0),
	Milk18(new byte[]{0x02,0x12},55,0),
	Milk19(new byte[]{0x02,0x13},40,0),
	Milk20(new byte[]{0x02,0x14},60,0),
	Milk21(new byte[]{0x02,0x15},45,0),
	Milk22(new byte[]{0x02,0x16},40,0),
	Milk23(new byte[]{0x02,0x17},40,0),
	Milk24(new byte[]{0x02,0x18},70,0),
	Milk25(new byte[]{0x02,0x19},45,0),
	Milk26(new byte[]{0x02,0x1A},45,0),
	Milk27(new byte[]{0x02,0x1B},50,0),
	Milk28(new byte[]{0x02,0x1C},37,0),
	Milk29(new byte[]{0x02,0x1D},40,0),
	Milk30(new byte[]{0x02,0x1E},50,0),
	Milk31(new byte[]{0x02,0x1F},55,0),
	Milk32(new byte[]{0x02,0x20},45,0),
	
	Coffee01(new byte[]{0x03,0x01},85,0),
	Coffee02(new byte[]{0x03,0x02},85,0),
	Coffee03(new byte[]{0x03,0x03},85,0),
	Coffee04(new byte[]{0x03,0x04},90,0),
	Coffee05(new byte[]{0x03,0x05},90,0),
	Coffee06(new byte[]{0x03,0x06},80,0),
	Coffee07(new byte[]{0x03,0x07},85,0),
	Coffee08(new byte[]{0x03,0x08},85,0),
	Coffee09(new byte[]{0x03,0x09},85,0),
	Coffee10(new byte[]{0x03,0x0A},85,0),
	Coffee11(new byte[]{0x03,0x0B},90,0),
	Coffee12(new byte[]{0x03,0x0C},85,0),
	Coffee13(new byte[]{0x03,0x0D},85,0),
	Coffee14(new byte[]{0x03,0x0E},85,0),
	Coffee15(new byte[]{0x03,0x0F},85,0),
	Coffee16(new byte[]{0x03,0x10},85,0),
	Coffee17(new byte[]{0x03,0x11},85,0),
	Coffee18(new byte[]{0x03,0x12},85,0),
	Coffee19(new byte[]{0x03,0x13},85,0),
	Coffee20(new byte[]{0x03,0x14},90,0),
	
	GreenTea01(new byte[]{0x05,0x01},85,0),
	GreenTea02(new byte[]{0x05,0x02},85,0),
	GreenTea03(new byte[]{0x05,0x03},90,0),
	GreenTea04(new byte[]{0x05,0x04},85,0),
	GreenTea05(new byte[]{0x05,0x05},95,0),
	GreenTea06(new byte[]{0x05,0x06},90,0),
	GreenTea07(new byte[]{0x05,0x07},85,0),
	GreenTea80(new byte[]{0x05,0x08},85,0),
	GreenTea09(new byte[]{0x05,0x09},75,0),
	
	BlackTea01(new byte[]{0x06,0x01},100,0),
	BlackTea02(new byte[]{0x06,0x02},100,0),
	BlackTea03(new byte[]{0x06,0x03},100,0),
	BlackTea04(new byte[]{0x06,0x04},95,0),
	BlackTea05(new byte[]{0x06,0x05},95,0),
	BlackTea06(new byte[]{0x06,0x06},95,0),
	BlackTea07(new byte[]{0x06,0x07},100,0),
	BlackTea08(new byte[]{0x06,0x08},100,0),
	BlackTea09(new byte[]{0x06,0x09},100,0),
	
	YellowTea01(new byte[]{0x07,0x01},95,0),
	YellowTea02(new byte[]{0x07,0x02},75,0),
	YellowTea03(new byte[]{0x07,0x03},80,0),
	YellowTea04(new byte[]{0x07,0x04},75,0),
	
	OolongTea01(new byte[]{0x08,0x01},100,0),
	OolongTea02(new byte[]{0x08,0x02},100,0),
	OolongTea03(new byte[]{0x08,0x03},100,0),
	OolongTea04(new byte[]{0x08,0x04},100,0),
	OolongTea05(new byte[]{0x08,0x05},100,0),
	OolongTea06(new byte[]{0x08,0x06},100,0),
	OolongTea07(new byte[]{0x08,0x07},100,0),
	OolongTea08(new byte[]{0x08,0x08},100,0),
	
	PuerTea01(new byte[]{0x09,0x01},100,0),
	PuerTea02(new byte[]{0x09,0x02},100,0),
	PuerTea03(new byte[]{0x09,0x03},100,0),
	
	WhiteTea01(new byte[]{0x0A,0x01},90,0),
	WhiteTea02(new byte[]{0x0A,0x02},80,0),
	WhiteTea03(new byte[]{0x0A,0x03},75,0),
	
	DarkTea01(new byte[]{0x0B,0x01},100,0),
	DarkTea02(new byte[]{0x0B,0x02},100,0),
	DarkTea03(new byte[]{0x0B,0x03},100,0),
	
	ScentedTea01(new byte[]{0x0C,0x01},90,0),
	ScentedTea02(new byte[]{0x0C,0x02},90,0),
	ScentedTea03(new byte[]{0x0C,0x03},100,0),
	ScentedTea04(new byte[]{0x0C,0x04},90,0);

	private Scene(byte[] sce, int tempC, int duration) {
		this.sceneCmd = sce;
		this.warmTemperatureC = tempC;
		this.warmDurationMinute = duration;
	}

	public byte[] getSceneCmd() {
		return sceneCmd;
	}
	
	public int getWarmTemperatureC() {
		return warmTemperatureC;
	}
	
	public int getWarmDurationMinute() {
		return warmDurationMinute;
	}

	public static int getIdBySceneCmd(byte[] cmd) {
		return getInt(cmd[0]) * getInt(SCENE_ARG) + getInt(cmd[1]);
	}

	public static Scene getSceneById(int Id) {
		for (Scene s : Scene.values()) {
			if (getIdBySceneCmd(s.getSceneCmd()) == Id)
				return s;
		}
		return null;
	}

	public static Scene getSceneByCmd(byte[] cmd) {
		if (cmd != null && cmd.length == 2)
			for (Scene s : Scene.values()) {
				if (s.getSceneCmd()[0] == cmd[0]
						&& s.getSceneCmd()[1] == cmd[1])
					return s;
			}
		return null;
	}

	public static int getSceneTitleResId(Context context, Scene s) {
		try {
			return context.getResources().getIdentifier(
					"scene_" + Scene.getIdBySceneCmd(s.getSceneCmd()),
					"string", context.getPackageName());
		} catch (Exception e) {
			return -1;
		}
	}

	public static int getSceneImgResId(Context context, Scene s, int sizeResId) {
		try {
			return context.getResources().getIdentifier(
					"scene_" + Scene.getIdBySceneCmd(s.getSceneCmd()) + "_"
							+ context.getString(sizeResId), "drawable",
					context.getPackageName());
		} catch (Exception e) {
			return -1;
		}
	}

	public static ArrayList<Scene> getLayerOneSceneList() {
		ArrayList<Scene> list = new ArrayList<Scene>();
		for (Scene s : Scene.values())
			if (s.getSceneCmd()[0] > 0 && s.getSceneCmd()[1] == 0)
				list.add(s);
		return list;
	}

	public static ArrayList<Scene> getLayerTwoSceneList(Scene one) {
		ArrayList<Scene> list = new ArrayList<Scene>();
		if(one.getSceneCmd()[1] != 0)
			return list;
		for (Scene s : Scene.values())
			if (s.getSceneCmd()[0] == one.getSceneCmd()[0]
					&& s.getSceneCmd()[1] != 0)
				list.add(s);
		return list;
	}

	public static ArrayList<Scene> getLayerTwoSceneList(){
		ArrayList<Scene> list = new ArrayList<Scene>();
		for (Scene s : Scene.values())
			if (s.getSceneCmd()[0] > 0 && getLayerTwoSceneList(s).size()== 0)
				list.add(s);
		return list;
	}
	
	public static ArrayList<Scene> searchSenseByKeyword(Context context, String key){
		ArrayList<Scene> list = new ArrayList<Scene>();
		String  sceneTitle;
		
		if(key==null||key.equals("")){
			//return list;
		}else if(key.matches(PREFIX)){
			for (Scene s : getLayerTwoSceneList()){
				sceneTitle = context.getString(Scene.getSceneTitleResId(context, s));
				if (HZPinyinHelper.getPinYinHeadChar(sceneTitle).startsWith(key.toLowerCase()))
					list.add(s);
				else if(HZPinyinHelper.getPinYin(sceneTitle).indexOf(key.toLowerCase())!=-1){
					list.add(s);
				}
			}
		}else {
			for (Scene s : getLayerTwoSceneList()){
				sceneTitle = context.getString(Scene.getSceneTitleResId(context, s));
				if (sceneTitle.indexOf(key)!=-1)
					list.add(s);
			}
		}
		return list;
	}
	
	private static int getInt(byte b) {
		return 0x000000FF & (int) b;
	}

	private byte[] sceneCmd;
	private int warmTemperatureC;
	private int warmDurationMinute;
	public static byte SCENE_ARG = 0x64;
	private static String PREFIX = "[a-zA-Z]{1,}";
}