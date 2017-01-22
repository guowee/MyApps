package com.wosai.proname.module.business.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.wosai.proname.common.utils.PageBean;
import com.wosai.proname.common.utils.QueryPageCallback;
import com.wosai.proname.module.business.entity.UserBall;

@Repository
public class UserBallDao extends BaseDAO {

	/**
	 * 根据用户名查询
	 * 
	 * @param userName
	 * @return
	 */
	public List<UserBall> findOsUserListByUserName(String userName) {
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder(
				" from UserBall user where user.name = ?");
		paramList.add(userName);
		List<UserBall> result = (List<UserBall>) this.query(hql.toString(),
				paramList.toArray());
		return result;
	}
	
	public long getUserBallCount() {
		return (Long) this.getRowCount("select count(ub.id) from UserBall ub", new Object[0]);
	}
	
	public long getUserBallCountByTime(String start_time, String end_time) {
		return (Long) this.getRowCount("select count(ub.id) from UserBall ub where ub.createDate>str_to_date('"+start_time+"','%Y-%m-%d %T') and ub.createDate<str_to_date('"+end_time+"','%Y-%m-%d %T')", new Object[0]);
	}
	
	public long getUserBallRank(int score) {
		List<Object> objList = new ArrayList<Object>();
		StringBuffer hsql=new StringBuffer();
		hsql.append("select count(ub.id) from UserBall ub where ub.score>="+score);
		objList.add(score);
		return (Long) this.getSingle(hsql.toString());
	}
	
	/**
	 * 获得分数前三
	 * @return
	 */
	public List<UserBall> getUserBallRank() {
		StringBuffer hsql=new StringBuffer();
		hsql.append("select ub from UserBall ub order by ub.score desc");
		
//		PageBean pageBean=new PageBean();
//		pageBean.setPage(0, 1);
//		pageBean.setCurrentPage(0);
//		return (List<UserBall>) this.query(hsql.toString(),pageBean);
		
		List<UserBall> userBalls=new ArrayList<UserBall>();
		
		// 第1名
		UserBall userBall = (UserBall)this.getSingle(hsql.toString());
		if(userBall != null){
			userBalls.add(userBall);
		}
		
		// 第100名
		PageBean pageBean=new PageBean();
		pageBean.setPage(0, 1);
		pageBean.setCurrentPage(100);
		List<UserBall> userBall100 = (List<UserBall>) this.query(hsql.toString(),pageBean);
		if(userBall100.size()>0){
			userBalls.add(userBall100.get(0));
		}
		
		return userBalls;
	}
	
	/**
	 * 获得某时段分数前三
	 * @return
	 */
	public List<UserBall> getUserBallRankByTime(String start_time, String end_time) {
		StringBuffer hsql=new StringBuffer();
		hsql.append("select ub from UserBall ub where ub.createDate>str_to_date('"+start_time+"','%Y-%m-%d %T') and ub.createDate<str_to_date('"+end_time+"','%Y-%m-%d %T') order by ub.score desc limit 3");
		PageBean pageBean=new PageBean();
		pageBean.setPage(0, 3);
		pageBean.setCurrentPage(0);
		return (List<UserBall>) this.query(hsql.toString(),pageBean);
	}

	public long getUserBallCountByDate(String start_date, String end_date) {
		return (Long) this.getRowCount("select count(ub.id) from UserBall ub where ub.createDate>str_to_date('"+start_date+"','%Y-%m-%d') and ub.createDate<str_to_date('"+end_date+"','%Y-%m-%d')", new Object[0]);
	}

	/**
	 * 获得某日期范围分数前三
	 * @return
	 */
	public List<UserBall> getUserBallRankByDate(String start_date, String end_date) {
		StringBuffer hsql=new StringBuffer();
//		 hsql.append("select ub from UserBall ub where ub.createDate>=str_to_date('"+start_date+"','%Y-%m-%d') and ub.createDate<str_to_date('"+end_date+"','%Y-%m-%d') and ub.phone!='' order by ub.score desc,ub.id");
//		
//		hsql.append("select ub from UserBall ub");
//		hsql.append(" where ub.createDate>=str_to_date('"+start_date+"','%Y-%m-%d') and ub.createDate<str_to_date('"+end_date+"','%Y-%m-%d')");
//		hsql.append(" and ub.phone!=''");
//		hsql.append(" GROUP BY phone");
//		hsql.append(" order by max(score) desc,id");
//		
//		PageBean pageBean=new PageBean();
//		pageBean.setPage(0, 100);
//		pageBean.setCurrentPage(0);
//		return (List<UserBall>) this.query(hsql.toString(),pageBean);
		
		
		// 分字段获取(上文无法显示max(score),需用此方法)
		hsql.append("select max(ub.nickname),max(ub.score),ub.phone");
		hsql.append(" from UserBall ub");
		hsql.append(" where ub.createDate>=str_to_date('"+start_date+"','%Y-%m-%d') and ub.createDate<str_to_date('"+end_date+"','%Y-%m-%d')");
		hsql.append(" and ub.phone!=''");
		hsql.append(" GROUP BY phone");
		hsql.append(" order by max(score) desc,id");
		
		PageBean pageBean=new PageBean();
		pageBean.setPage(0, 100);
		pageBean.setCurrentPage(0);
		
		//List<Object[]> objs = (List<Object[]>) this.query(hsql.toString(),pageBean);
		List<Object[]> objs = (List<Object[]>) this.getHibernateTemplate().execute(
				new QueryPageCallback(hsql.toString(), new Object[] {}, pageBean));
		List<UserBall> userBalls= new ArrayList<UserBall>();
		for (Object[] object : objs) {
			UserBall userBall=new UserBall();
			userBall.setNickname(String.valueOf(object[0]));
			userBall.setScore(String.valueOf(object[1]));
			userBall.setPhone(String.valueOf(object[2]));
//			userBall.setNickname("aa");
//			userBall.setScore("100");
//			userBall.setPhone("10086");
			userBalls.add(userBall);
		}
		return userBalls;
	}
	
}
