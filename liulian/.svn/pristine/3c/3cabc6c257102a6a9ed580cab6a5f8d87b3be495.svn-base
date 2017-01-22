package com.haomee.util;

import java.util.LinkedList;

/**
 * 任务栈,后进先出
 *
 */
public class TaskStack {

	private LinkedList<Integer> stack;
	
	public TaskStack() {
		stack = new LinkedList<Integer>();
	}
	
	
	public void push(int i){
		if(i!=getTop()){		// 防止连续重复添加
			stack.add(i);
		}
	}
	
	public int pop(){		
		int top = getTop();
		if(top!=-1){
			stack.removeLast();	
		}
		return top;
	}
	
	public int getTop(){
		if(stack.size()==0){
			return -1;
		}
		
		int top = stack.getLast();
		
		return top;
	}
	
	public void clear(){
		stack.clear();
	}
	
	public String printAll(){
		
		StringBuffer str = new StringBuffer();
		for(int i:stack){
			str.append(i+",");
		}
		
		return str.toString();
	}
	
}
