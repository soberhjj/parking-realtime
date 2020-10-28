package com.newland.iotshow.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newland.iotshow.conf.SqlConstant;
import com.newland.iotshow.model.kafka.Offset;
import com.newland.iotshow.util.DBConnectionManager;

public class OffsetQueryService {

	private DBConnectionManager dbManager = null;
	
	public OffsetQueryService(){
		this.dbManager = DBConnectionManager.getInstance();
	}
	
	/**
	 * 查询指定topic的消费情况
	 * @param topic
	 * @param consumerId
	 * @return
	 * @throws SQLException 
	 */
	public List<Offset> query(String topic,String consumerId) throws SQLException{
		List<Offset> offsetList = null;
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = this.dbManager.getConnection();
			ps = connection.prepareStatement(SqlConstant.SQL_QUERY_OFFSET);
			ps.setString(1,topic);
			ps.setString(2,consumerId);
			ResultSet rs = ps.executeQuery();
			offsetList = new ArrayList<Offset>();
			Offset offset = null;
			while(rs.next()){
				offset = new Offset();
				offset.setTopic(rs.getString("topic"));
				offset.setPartition(rs.getInt("partition"));
				offset.setOffset(rs.getLong("offset"));
				offsetList.add(offset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(ps != null){
				ps.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		return offsetList;
	}
}
