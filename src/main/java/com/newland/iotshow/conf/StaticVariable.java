package com.newland.iotshow.conf;

import scala.tools.nsc.backend.icode.Opcodes;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 静态变量
 * @Author: Ljh
 * @Date 2020/9/18 13:54
 */
public class StaticVariable {

    /**
     * 当前日期，用于标记日期是否变化，变化的话需要更新库里的flow为0
     */
    public static String day = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());

}
