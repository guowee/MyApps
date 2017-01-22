package com.wosai.proname.module.business.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wosai.proname.common.utils.GodUtils;
import com.wosai.proname.common.utils.MD5Tool;
import com.wosai.proname.module.business.dao.UserBallDao;
import com.wosai.proname.module.business.entity.UserBall;
import com.wosai.proname.module.business.entity.WebError;
import com.wosai.proname.module.business.service.UserBallService;

@Service("UserBallService")
public class UserBallServiceImpl implements UserBallService {

	@Resource
	private UserBallDao userBallDao;

	@Override
	public UserBall checkLogin(UserBall user) {
		List<UserBall> userList = userBallDao.findOsUserListByUserName(user
				.getName());
		if (GodUtils.CheckListNull(userList)) {
			return null;
		}

		UserBall dbUser = userList.get(0);
		if (dbUser.getPasswd().equals(MD5Tool.ToMD5(user.getPasswd()))) {
			return dbUser;
		}
		return null;
	}

	public UserBall checkUserName(UserBall user) {
		List<UserBall> userList = userBallDao.findOsUserListByUserName(user
				.getName());
		if (GodUtils.CheckListNull(userList)) {
			return null;
		}

		UserBall dbUser = userList.get(0);
		return dbUser;
	}

	@Override
	public void regUser(UserBall user) {
		userBallDao.saveOrUpdate(user);
	}

	@Override
	public UserBall getUserBallByUserName(String userName) {
		List<UserBall> userList = userBallDao.findOsUserListByUserName(userName);
		if (GodUtils.CheckListNull(userList)) {
			return null;
		}
		return userList.get(0);
	}

	@Override
	public UserBall getUserBallById(Integer id) {
		return (UserBall) userBallDao.getObjectById(UserBall.class, id);
	}
	
	public long getUserBallCount() {
		return userBallDao.getUserBallCount();
	}
	
	@Override
	public long getUserBallCountByTime(String start_time, String end_time) {
		return userBallDao.getUserBallCountByTime(start_time, end_time);
	}
	
	public long getUserBallRank(int score) {
		return userBallDao.getUserBallRank(score);
	}

	@Override
	public List<UserBall> getUserBallRank() {
		return userBallDao.getUserBallRank();
	}

	@Override
	public List<UserBall> getUserBallRankByTime(String start_time, String end_time) {
		return userBallDao.getUserBallRankByTime(start_time, end_time);
	}

	@Override
	public long getUserBallCountByDate(String start_date, String end_date) {
		return userBallDao.getUserBallCountByDate(start_date, end_date);
	}
	
	@Override
	public List<UserBall> getUserBallRankByDate(String start_date, String end_date) {
		return userBallDao.getUserBallRankByDate(start_date, end_date);
	}

	@Override
	public void reportWebError(WebError webError) {
		userBallDao.saveOrUpdate(webError);
	}

}
