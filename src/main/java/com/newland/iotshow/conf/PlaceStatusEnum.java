package com.newland.iotshow.conf;

/**
 * @Description: 停车状态枚举类
 * @Author: Ljh
 * @Date 2020/9/14 13:49
 */
public enum PlaceStatusEnum {

    ENOUGH(0F,"车位充足"),
    FEWER(0.7F,"车位较少"),
    FULL(1F,"车位已满");

    private float pressure;
    private String status;

    private PlaceStatusEnum(float pressure,String status){
        this.pressure = pressure;
        this.status = status;
    }

    public float getPressure(){
        return pressure;
    }

    public String getStatus(){
        return status;
    }
}
