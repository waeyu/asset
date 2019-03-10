package com.sds.afi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.sds.afi.dao.paging.IPagingSQLGenerator;
import com.sds.afi.dao.paging.PaginationVO;
import com.sds.afi.dao.paging.PagingSQLGeneratorFactory;

public abstract class AbstractPagingDAO extends AbstractOffsetLimitDAO {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private IPagingSQLGenerator pagingSqlGenerator;

	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);

		// set pagingSqlGenerator
		this.pagingSqlGenerator = PagingSQLGeneratorFactory.createPagingSQLGenerator(dataSource);
	}
	
	/* ------------------------------------------------------------------------- */

	/**
	 * @brief
	 * 
	 * @param selectQuery
	 * @param param
	 *            : Map 또는 VO 형태 가능
	 * @param rowMapper
	 * @return 결과 List 는 null 이 아님. 단 isEmpty() 일 수 있음.
	 * @note 동일하게 log 를 남기기 위해 override 함 (동작 상의 차이는 없음)
	 */
	@Override
	protected <T, R> List<R> executeSelectQuery(String selectQuery, T param, final RowMapper<R> rowMapper) {
		return executePagingSelectQuery(selectQuery, param, rowMapper, (PaginationVO) null);
	}

	/**
	 * @brief
	 * 
	 * @param selectQuery
	 * @param param
	 *            : Map 또는 VO 형태 가능
	 * @param rowMapper
	 * @param pagingVo
	 *            : 페이징 정보를 주고 받는 VO <br/>
	 *            페이징 미 사용시 null
	 * @return 결과 List 는 null 이 아님. 단 isEmpty() 일 수 있음.
	 */
	protected <T, R> List<R> executePagingSelectQuery(String selectQuery, T param, final RowMapper<R> rowMapper,
			PaginationVO pagingVo) {

		// create parameter map
		Map<String, Object> paramMap = createParamMap(selectQuery, param, pagingVo);

		// create select query
		String selectQuery2 = makeQueryFromVelocityString(selectQuery, paramMap, pagingVo);

		// create parameter source
		SqlParameterSource paramSrc = createParamSrc(paramMap, param);

		sqlLogWithParameter(selectQuery2, paramSrc);

		try {
			long t1 = System.currentTimeMillis();

			// c.f. result cannot be null.
			List<R> result = (List<R>) this.jdbcTemplate.query(selectQuery2, paramSrc, rowMapper);

			long t2 = System.currentTimeMillis();
			long selectTime = t2 - t1;
			if (selectTime > minSelectTime) {
				log.warn("select: " + result.size() + " records, " + selectTime + " ms. ** warning **");
//			} else {
//				log.debug("select: " + result.size() + " records, " + selectTime + " ms.");
			}

			return result;

		} catch (RuntimeException e) {
			sqlErrorLog(e, selectQuery2, param);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> createParamMap(String selectQuery, Object param, PaginationVO pagingVo) {
		/*
		 * paramMap 생성 조건 : 페이징이 있거나, 다이나믹 쿼리 이거나, 원래 Map 이거나
		 */
		if (param == null && pagingVo != null && pagingSqlGenerator != null) {
			log.debug("Paging : " + pagingVo.toString());
			return new HashMap<String, Object>();
		} else if (pagingVo != null && pagingSqlGenerator != null) {
			log.debug("Paging : " + pagingVo.toString());
			return getMapFromVO(param);
		} else if (isDynamicQuery(selectQuery)) {
			return getMapFromVO(param);
		} else if (param instanceof Map) {
			return (Map<String, Object>) param;
		}
		return null;
	}

	protected String makeQueryFromVelocityString(String selectQuery, Map<String, Object> paramMap, PaginationVO pagingVo) {
		String selectQuery2 = makeQueryFromVelocityString(selectQuery, paramMap);
		String selectQuery3 = null;
		if (pagingVo != null && pagingSqlGenerator != null) {

			// Total Record Count
			if (pagingVo.isCountRecordSize()) {
				// get Count Query
				String countQuery = pagingSqlGenerator.getCountSQL(selectQuery2);
				long recordCount = executeSelectOneQuery(countQuery, paramMap, Long.class);
				pagingVo.setRecordCount(recordCount);
				pagingVo.checkPageIndex();
			}

			// get Paging Query
			selectQuery3 = pagingSqlGenerator.getPaginationSQL(selectQuery2, paramMap, pagingVo.getPageIndex(),
					pagingVo.getPageSize());

		} else {
			selectQuery3 = selectQuery2;
		}

		return selectQuery3;
	}

	/**
	 * @brief BeanPropertyRowMapper 를 사용하는 메서드<br/>
	 *        parameter 클래스와 result 클래스가 동일한 경우 사용
	 * @note sql 문자열 안에 :abc 로 변수를 썼다면, paramVo 클래스에 getAbc() 메서드가 있어야 함. <br/>
	 *       또한 쿼리 결과에 XY_Z 라는 컬럼이 있다면 paramVo 클래스에 setXyZ() 메서드가 있어야 함
	 * @param selectQuery
	 * @param paramVo
	 *            : VO 형태 가능
	 * @param pagingVo
	 *            : 페이징 정보를 주고 받는 VO <br/>
	 *            페이징 미 사용시 null
	 * @return
	 */
	protected <T> List<T> executePagingSelectQuery(String selectQuery, T paramVo, PaginationVO pagingVo) {
		@SuppressWarnings("unchecked")
		Class<T> resultVoClass = (Class<T>) paramVo.getClass();
		RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(resultVoClass);
		return executePagingSelectQuery(selectQuery, paramVo, rowMapper, pagingVo);
	}
	
	/**
	 * @brief BeanPropertyRowMapper 를 사용하는 메서드 <br/>
	 *        parameter 클래스와 result 클래스가 다른 경우 사용
	 * @note sql 문자열 안에 :abc 로 변수를 썼다면, param 클래스에 getAbc() 메서드가 있어야 함. <br/>
	 *       또한 쿼리 결과에 XY_Z 라는 컬럼이 있다면 resultClass 에 setXyZ() 메서드가 있어야 함
	 * 
	 * @param selectQuery
	 * @param param
	 *            : Map 또는 VO 형태 가능. null 가능
	 * @param resultClass
	 *            : VO 클래스. <br/>
	 *            또는 single column 인 경우, String.class, Integer.class, Long.class, Timestamp.class 가능
	 * @param pagingVo
	 *            : 페이징 정보를 주고 받는 VO <br/>
	 *            페이징 미 사용시 null
	 * @return 결과 List 는 null 이 아님. 단 isEmpty() 일 수 있음.
	 */
	protected <T, R> List<R> executePagingSelectQuery(String selectQuery, T param, Class<R> resultClass, PaginationVO pagingVo) {
		RowMapper<R> rowMapper = null;
		if (isSingleColumnType(resultClass)) {
			rowMapper = new SingleColumnRowMapper<R>(resultClass);
		} else {
			rowMapper = BeanPropertyRowMapper.newInstance(resultClass);
		}
		return executePagingSelectQuery(selectQuery, param, rowMapper, pagingVo);
	}
	
}
