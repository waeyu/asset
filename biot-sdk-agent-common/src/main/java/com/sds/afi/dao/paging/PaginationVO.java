package com.sds.afi.dao.paging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaginationVO {

	private static final Logger LOG = LoggerFactory.getLogger(PaginationVO.class);

	private int pageSize = 20;

	private int pageIndex = 1;

	private long recordCount = -1;

	private boolean countRecordSize = true;

	public PaginationVO() {
		super();
	}

	public PaginationVO(int pageSize) {
		setPageSize(pageSize);
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize < 1) {
			LOG.warn("Page size must have over 1. (current page size = " + pageSize + ")");
		}
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		if (pageIndex < 1) {
			LOG.warn("Page number must have over 1. (current page number = " + pageIndex + ")");
		}
		this.pageIndex = pageIndex;
	}

	public long getRecordCount() {
		return this.recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public boolean isCountRecordSize() {
		if (countRecordSize && recordCount == -1) {
			return true;
		}
		return false;
	}

	public void setCountRecordSize(boolean countRecordSize) {
		this.countRecordSize = countRecordSize;
	}

	/*
	 * 
	 */
	
	public void checkPageIndex() {
		int lastPageIndex = getPageCount();
		if (this.pageIndex <= lastPageIndex) {
			return;
		}
		if (lastPageIndex != 0) {
			LOG.error("Current page number is bigger than last page number of result. (current page number : "
					+ this.pageIndex + ", last page number : " + getPageCount() + ")");
		}
	}

	public int getPageCount() {
		if (this.pageSize == 0) {
			return (int) this.recordCount;
		}
		if (this.recordCount % this.pageSize == 0L) {
			return (int) (this.recordCount / this.pageSize);
		}
		return (int) (this.recordCount / this.pageSize + 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaginationVO [pageSize=").append(pageSize).append(", pageIndex=").append(pageIndex)
				.append(", recordCount=").append(recordCount).append(", countRecordSize=").append(countRecordSize)
				.append("]");
		return builder.toString();
	}

}