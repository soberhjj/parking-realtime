package com.newland.iotshow.conf;
/**
 * sql定义
 *
 * @author Ljh
 *
 */
public interface SqlConstant {
	
	String SQL_QUERY_OFFSET = "select topic,`partition`,offset from kafka_offset where topic = ? and consumer_id = ?";
	
	String SQL_UPDATE_OFFSET = "insert into kafka_offset(topic,`partition`,consumer_id,offset) "
			+ "values(?,?,?,?) "
			+ "ON DUPLICATE KEY update "
			+ "offset = values(offset)";

//	String SQL_UPDATE_PARKING_PLACE_STATUS = "update parkinglot_place_realtime_status set total_place=?, free_place=?, reserved_place=?, parked_place=?, " +
//			"pressure=?, status=?, update_time=? where id=?";
	String SQL_UPDATE_PARKING_PLACE_STATUS = "update parkinglot_place_realtime_status set free_place=?, reserved_place=?, parked_place=?, " +
		"pressure=?, status=?, update_time=? where id=?";

	String SQL_UPDATE_PARKING_FLOW  = "update parkinglot_place_realtime_status set flow=flow+1 where id=?";

	String SQL_SELECT_PLACE_UPDATE_TIME = "select update_time from parkinglot_place_realtime_status where id=?";

	String SQL_RESET_PARKING_FLOW = "update parkinglot_place_realtime_status set flow=0";

	String SQL_SELECT_PARK_NAME = "select name from parkinglot_place_realtime_status where id=?";

	String SQL_INSERT_PARKINGLOT_ACCESS_RECORD = "insert into parkinglot_access_record(park_id,park_name,park_no,car_plate,access_type,access_time) "
			+ "values(?,?,?,?,?,?) ";

	//由于PreparedStatement无法用?代表列名，以下用%s表示列名，并用String.format格式化
	String SQL_SELECT_PLACE_STATUS_COLUMN1_BY_COLUMN2 = "select %s from parkinglot_place_realtime_status where %s=?";

}
