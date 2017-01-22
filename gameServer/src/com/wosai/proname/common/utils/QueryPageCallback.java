/**
 * @{#} QueryPageCallback.java Create on 2006-7-23 10:57:10
 *
 * Copyright (c) 2006 by Onewave Inc.
 */
package com.wosai.proname.common.utils;

/**
 * 2014-8-26 ионГ11:25:46
 * 
 */
public class QueryPageCallback extends QueryCallback {
	public QueryPageCallback(String queryString, Object[] params,
			PageBean pageBean) {
		super(queryString, params, pageBean.getCurrentPageFirstRecord(),
				pageBean.getCurrentPageSize());
	}
}
