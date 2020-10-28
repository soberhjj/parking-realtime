package com.newland.iotshow.model.mysql;

import lombok.Data;


/**
 * @Description: 停车场及其车位状态信息(mysql宽表)
 * @Author: Ljh
 * @Date 2020/9/13 17:15
 */
@Data
public class ParkingLotPlaceRealTimeStatus {
    private String id;

    private String name;

    private String location;

    private Integer totalPlace;

    private Integer freePlace;

    private Integer reservedPlace;

    private Integer parkedPlace;

    private Float pressure;

    private String status;

    private Integer flow;

    private Long createTime;

    private Long updateTime;
}
