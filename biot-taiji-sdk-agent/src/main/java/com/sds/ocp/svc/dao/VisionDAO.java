package com.sds.ocp.svc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.svc.vo.Vision;

@Repository
public class VisionDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<Vision> list() {
		
		logger.debug("list begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM vision_players " ); 
		
		Map<String, Object> param = new HashMap<>();
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new VisionRowMapper());
		
	}
	
	private static class VisionRowMapper implements RowMapper<Vision> {
		
		@Override
		public Vision mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Vision vision = new Vision();

			vision.setPid(rs.getString("pid"));
			vision.setPip(rs.getString("pip"));
			vision.setPname(rs.getString("pname"));
			vision.setPver(rs.getString("pver"));
			vision.setPvolume(rs.getString("pvolume"));
			vision.setPmac(rs.getString("pmac"));
			vision.setPstatus(rs.getInt("pstatus"));

			return vision;
		}

	}
	

}
