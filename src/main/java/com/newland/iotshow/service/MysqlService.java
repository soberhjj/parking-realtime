package com.newland.iotshow.service;

import com.newland.iotshow.conf.Constant;
import com.newland.iotshow.conf.SqlConstant;
import com.newland.iotshow.model.kafka.AccessParkingLotInfo;
import com.newland.iotshow.model.mysql.ParkingLotPlaceRealTimeStatus;
import com.newland.iotshow.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @Author: Ljh
 * @Date 2020/7/22 11:11
 */
public class MysqlService {

    /**
     * 更新车位实时实时状态
     * @Author Ljh
     * @Date 2020/9/14 14:35
     * @param status
     * @return void
     */
    public void updateParkingLotPlaceRealTimeStatus(ParkingLotPlaceRealTimeStatus status) throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_UPDATE_PARKING_PLACE_STATUS);
//            stmt.setInt(1,status.getTotalPlace());
//            stmt.setInt(2,status.getFreePlace());
//            stmt.setInt(3,status.getReservedPlace());
//            stmt.setInt(4,status.getParkedPlace());
//            stmt.setFloat(5,status.getPressure());
//            stmt.setString(6,status.getStatus());
//            stmt.setLong(7,status.getUpdateTime());
//            stmt.setString(8,status.getId());

            stmt.setInt(1,status.getFreePlace());
            stmt.setInt(2,status.getReservedPlace());
            stmt.setInt(3,status.getParkedPlace());
            stmt.setFloat(4,status.getPressure());
            stmt.setString(5,status.getStatus());
            stmt.setLong(6,status.getUpdateTime());
            stmt.setString(7,status.getId());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally{
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }

    /**
     * 更新停车指定停车场id的流量(执行一次+1)
     * @Author Ljh
     * @Date 2020/9/14 14:35
     * @param id
     * @return void
     */
    public void updateParkingFlow(String id) throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_UPDATE_PARKING_FLOW);
            stmt.setString(1,id);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally{
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }

    /**
     * 根据停车场id查询update_time
     * @Author Ljh
     * @Date 2020/9/18 14:10
     * @param id
     * @return java.lang.Long
     */
    public Long selectUpdateTimeById(String id) throws Exception{
        Long result = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_SELECT_PLACE_UPDATE_TIME);
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            while (rs.next()){
                result = rs.getLong("update_time");
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally{
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }

    /**
     * 重置停车流量为0(当日期发生改变时使用)
     * @Author Ljh
     * @Date 2020/9/18 14:05
     *
     * @return boolean
     */
    public void resetParkingFlow() throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_RESET_PARKING_FLOW);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally{
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }

    /**
     * 根据停车场id查询name
     * @Author Ljh
     * @Date 2020/9/18 14:10
     * @param id
     * @return java.lang.Long
     */
    public String selectParkNameById(String id) throws Exception{
        String result = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_SELECT_PARK_NAME);
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            while (rs.next()){
                result = rs.getString("name");
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally{
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }

    /**
     * 插入车辆进出场记录
     * @Author Ljh
     * @Date 2020/9/28 20:11
     * @param accessParkingLotInfo
     * @return void
     */
    public void insertParkinglotAccessRecord(AccessParkingLotInfo accessParkingLotInfo) throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            stmt = conn.prepareStatement(SqlConstant.SQL_INSERT_PARKINGLOT_ACCESS_RECORD);
            stmt.setString(1,accessParkingLotInfo.getParkId());
            // 根据id找name,name为空直接跳过该记录
            String parkName = selectParkNameById(accessParkingLotInfo.getParkId());
            if(parkName == null){
                return;
            }
            stmt.setString(2,parkName);
            stmt.setString(3,accessParkingLotInfo.getParkNo());
            stmt.setString(4,accessParkingLotInfo.getCarPlate());
            // 离场时间为null，说明是进场数据
            if(accessParkingLotInfo.getLeaveTime() == null){
                stmt.setString(5,"进场");
                stmt.setString(6,accessParkingLotInfo.getEnterTime());
            }else {
                stmt.setString(5,"离场");
                stmt.setString(6,accessParkingLotInfo.getLeaveTime());
            }

            stmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally{
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }

    }


    /**
     * 查询停车场及车位状态表数据(根据指定的列查找列)
     * @Author Ljh
     * @Date 2020/10/9 9:21
     * @param resultColumnName
     * @param conditionColumnName
     * @param conditionValue
     * @return java.lang.Object
     */
    public Object selectParkinglotColumn1ByColumn2(String resultColumnName,String conditionColumnName,Object conditionValue) throws Exception{
        Object result = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            String sql = String.format(SqlConstant.SQL_SELECT_PLACE_STATUS_COLUMN1_BY_COLUMN2,resultColumnName,conditionColumnName);
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1,conditionValue);

            rs = stmt.executeQuery();
            while (rs.next()){
                result = rs.getObject(resultColumnName);
                System.out.println("result: "+result);
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally{
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
    }
}
