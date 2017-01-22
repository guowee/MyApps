package com.wosai.proname.common.utils;

import java.io.Serializable;

/**
 * 翻页设置.
 * 
 * @author Coffee
 */
public class PageBean implements Serializable {
	/** Comment for <code>serialVersionUID</code> . */
	private static final long serialVersionUID = -8132783218617126174L;
	private int pageCount;
	private int pageSize = 20;
	private int lastPageSize;
	private long recordCount;
	private int currentPage = 1;

	/**
	 * Creates a new PageBean object.
	 */
	public PageBean() {
		super();
	}

	/**
	 * Creates a new PageBean object.
	 * 
	 * @param pageSize
	 */
	public PageBean(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 
	 * @param pageCount
	 */
	protected void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 
	 * @param recordCount
	 */
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;

		if (this.pageSize != 0) {
			this.calculate();
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return this.currentPage;
	}

	/**
	 * 
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 
	 * @return
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 设计需要多少页.
	 */
	public void calculate() {
		this.pageCount = (int) recordCount / pageSize;

		this.lastPageSize = (int) this.recordCount % this.pageSize;

		if (this.lastPageSize == 0) {
			this.lastPageSize = this.pageSize;
		} else {
			this.pageCount++;
		}
	}

	/**
	 * 设置页.
	 * 
	 * @param recordCount
	 * @param pageSize
	 */
	public void setPage(long recordCount, int pageSize) {
		this.recordCount = recordCount;
		this.pageSize = pageSize;
		calculate();
	}

	/**
	 * 创建新的页.
	 * 
	 * @param recordCount
	 * @param pageSize
	 * 
	 * @return
	 */
	public static PageBean createPageBean(long recordCount, int pageSize) {
		PageBean bean = new PageBean();
		bean.setPage(recordCount, pageSize);

		return bean;
	}

	/**
	 * 得到总共有多少条记录.
	 * 
	 * @return
	 */
	public long getRecordCount() {
		return ((pageCount - 1) * pageSize) + lastPageSize;
	}

	/**
	 * 最后一页的大小.
	 */
	public int getLastPageSize() {
		return this.lastPageSize;
	}

	/**
	 * 
	 * @param lastPageSize
	 */
	public void setLastPageSize(int lastPageSize) {
		this.lastPageSize = lastPageSize;
	}

	/**
	 * 总的页数.
	 * 
	 * @return
	 */
	public int getPageCount() {
		return this.pageCount;
	}

	/**
	 * 当前的页数.
	 * 
	 * @return
	 */
	public int getCurrentPageSize() {
		if (this.pageCount == this.currentPage) {
			return this.getLastPageSize();
		} else {
			return this.pageSize;
		}
	}

	/**
	 * 得到当前页的第一条记录的记录数.
	 * 
	 * @return
	 */
	public int getCurrentPageFirstRecord() {
		return (this.currentPage - 1) * this.pageSize;
	}

	/**
	 * 下一页.
	 * 
	 * @return
	 */
	public int getNextPageNo() {
		int result = this.currentPage + 1;

		if (result > this.pageCount) {
			result = this.pageCount;
		}

		return result;
	}

	/**
	 * 上一页.
	 * 
	 * @return
	 */
	public int getPrivPageNo() {
		int result = this.currentPage - 1;

		if (result < 1) {
			result = 1;
		}

		return result;
	}

	/**
	 * 是否还有下一页.
	 * 
	 * @return
	 */
	public boolean isNext() {
		return this.pageCount > this.currentPage;
	}

	/**
	 * 是否还有上一页.
	 * 
	 * @return
	 */
	public boolean isPriv() {
		return this.currentPage > 1;
	}

	/**
	 * 是否是最后一页.
	 * 
	 * @return
	 */
	public boolean isLast() {
		return this.pageCount <= this.currentPage;
	}

	/**
	 * 是否是第一页.
	 * 
	 * @return
	 */
	public boolean isFirst() {
		return this.currentPage == 1;
	}

	/**
	 * 设置当前页.
	 * 
	 * @return
	 */
	public int[] getCurrentScope() {
		if (pageCount == 0) {
			return new int[] { 0, 0 };
		}

		int[] result = new int[2];

		if (this.currentPage > this.pageCount) {
			this.currentPage = this.pageCount;
		}

		result[0] = (this.currentPage - 1) * pageSize;
		result[1] = result[0] + pageSize;

		if (this.currentPage == this.pageCount) {
			result[1] = result[0] + this.lastPageSize;
		}

		return result;
	}

	/**
	 * 得到记录数.
	 * 
	 * @return PageBean.recordCount
	 */
	public int getIntegerRecordCount() {
		return (int) this.recordCount;
	}
}
