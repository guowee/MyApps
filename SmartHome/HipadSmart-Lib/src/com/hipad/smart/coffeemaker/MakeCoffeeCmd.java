/**
 * MakeCoffeeCmd.java 2014-12-17
 */
package com.hipad.smart.coffeemaker;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class MakeCoffeeCmd extends CoffeeMakerCmd {
	
	private MakeCoffeeCmdBody mCmdBody;
	
	public MakeCoffeeCmd() {
		mCmdBody = new MakeCoffeeCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_MAKE_COFFEE;
	}
	
	public void chooseCup(boolean big){
		mCmdBody.chooseCup(big);
	}
	
	public void setTempC(byte temperature){
		mCmdBody.setTempC(temperature);
	}
	
	public void setBigVolume(byte volume){		
		mCmdBody.setBigVolume(volume);
	}
	
	public void setSmallVolume(byte volume){		
		mCmdBody.setSmallVolume(volume);
	}
	
	private final static class MakeCoffeeCmdBody extends MsgBodyData{

		public MakeCoffeeCmdBody() {
			super(new byte[]{0, (byte) 0xFF, 0, 0, 0});
		}
		
		public void chooseCup(boolean big){
			setByte(0, (byte) (big ? 1 : 2));
		}
		
		public void setTempC(byte temperature){
			setByte(2, temperature);
		}
		
		public void setBigVolume(byte volume){
			setByte(3, volume);
		}
		
		public void setSmallVolume(byte volume){
			setByte(4, volume);
		}
	}

}
