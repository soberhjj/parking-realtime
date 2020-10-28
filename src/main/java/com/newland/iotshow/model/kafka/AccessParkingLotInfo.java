package com.newland.iotshow.model.kafka;

import lombok.Data;

/**
 * @Description: 车辆进出场信息(kafka)
 * @Author: Ljh
 * @Date 2020/9/13 16:12
 */
@Data
public class AccessParkingLotInfo {
    private String id;
    private String parkId;
    private String parkNo;
    private String carPlate;
    private String enterTime;
    private Integer isLeft;
    private String leaveTime;
    private Integer parkPay;
}
