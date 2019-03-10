
package com.sds.afi.dao.paging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.util.StringUtil;

public class OffsetLimitVO {
	private static final Logger		LOG					= LoggerFactory.getLogger(OffsetLimitVO.class);
	private static final boolean	countTotalSize		= true;

	private final long				offset;
	private final long				limit;
	private long					total				= -1;
	private boolean					countRecordSize	;

	public OffsetLimitVO() {
		this.offset = 0L;
		this.limit = 0L; // limit=0 이면 unlimit
	}

	public OffsetLimitVO(String offsetStr, String limitStr) {

		long offset = StringUtil.isEmpty(offsetStr) ? 0L : Long.parseLong(offsetStr);
		long limit = StringUtil.isEmpty(limitStr) ? 1L : Long.parseLong(limitStr);

		if (offset < 0L || limit < 0L) {
			throw new IllegalArgumentException("Invalid offset or limit value. offset=[" + offset + "], limit=[" + limit + "]");
		}
		this.offset = offset;
		this.limit = limit; // limit=0 이면 unlimit
	}

	public OffsetLimitVO(long offset, long limit) {
		if (offset < 0L || limit < 0L) {
			throw new IllegalArgumentException("Invalid offset or limit value. offset=[" + offset + "], limit=[" + limit + "]");
		}
		this.offset = offset;
		this.limit = limit; // limit=0 이면 unlimit
	}

	public long getOffset() {
		return offset;
	}

	public long getLimit() {
		return limit;
	}

	public boolean isCountRecordSize() {
		if (countTotalSize && total == -1) {
			countRecordSize = true;
			return countRecordSize;
		}
		countRecordSize = false;
		return countRecordSize;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void checkPageIndex() {
		long limit = getLimit();
		if (this.total >= limit) {
			return;
		}
		if (limit != 0) {
			LOG.info("Current request limit is bigger than having limit number of result. (reqeust limit number : " + limit
					+ ", total : " + this.total + ")");
		}
	}

	@Override
	public String toString() {
		return "OffsetLimitVO [offset=" + offset + ", limit=" + limit + "]";
	}

}
