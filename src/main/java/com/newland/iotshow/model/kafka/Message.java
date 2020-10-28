package com.newland.iotshow.model.kafka;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Description: kafka中的消息bean
 * @Author: Ljh
 * @Date 2020/9/13 15:57
 */
@Data
public class Message {
    private String appId;
    private Integer timestamp;
    private String version;
    private String signMethod;
    private String sign;
    private Integer reportType;
    private ArrayList reportData;
}
