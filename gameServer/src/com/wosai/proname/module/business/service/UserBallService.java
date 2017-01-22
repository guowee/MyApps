package com.wosai.proname.module.business.service;

import java.util.Date;
import java.util.List;

import com.wosai.proname.module.business.entity.UserBall;
import com.wosai.proname.module.business.entity.WebError;


public interface UserBallService {
	
	/**
	 * 登录
	 * 
	 * @param user
	 * @return
	 */
	public UserBall checkLogin(UserBall user);

	/**
	 * 检测用户名是否已经存在
	 * @param user
	 * @return
	 */
	public UserBall checkUserName(UserBall user);
	
	/**
	 * @param userName
	 * @return
	 */
	public UserBall getUserBallByUserName(String userName);

	/**
	 * @param id
	 * @return
	 */
	public UserBall getUserBallById(Integer id);

	void regUser(UserBall user);
	
	void reportWebError(WebError webError);
	
	public long getUserBallCount();
	
	public long getUserBallCountByTime(String start_time, String end_time);
	
	public long getUserBallRank(int score);

	public List<UserBall> getUserBallRank();
	
	public List<UserBall> getUserBallRankByTime(String start_time, String end_time);
	
	public long getUserBallCountByDate(String start_date, String end_date);
	
	public List<UserBall> getUserBallRankByDate(String start_date, String end_date);
}
