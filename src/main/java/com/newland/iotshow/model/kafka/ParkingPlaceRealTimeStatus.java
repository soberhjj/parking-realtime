package com.newland.iotshow.model.kafka;

import lombok.Data;

/**
 * @Description: 停车场车位实时状态(kafka)
 * @Author: Ljh
 * @Date 2020/9/13 16:20
 */
@Data
public class ParkingPlaceRealTimeStatus {
    private String parkId;
    private Integer totalPlace;
    private Integer freePlace;
    private Integer reservedPlace;
    private Integer parkedPlace;
    private Long updateTime;
}
