package com.newland.iotshow.util;

import com.newland.iotshow.conf.Constant;

/**
 * @Description:
 * @Author: Ljh
 * @Date 2019/12/20 14:44
 */
public class StringUtil {
    /**
     * @Description: 判断字段是否为空(包括空值,空字符串,"null")
     * @Author Ljh
     * @Date 2019/12/18 9:47
     * @param column
     * @return boolean{true:空，false:非空}
     */
    public static boolean judgeEmptyOrNull(String column){
        boolean isEmptyOrNull = false;
        if(null == column || Constant.EMPTY.equals(column) || Constant.DEFAULT_STRING.equals(column)){
            isEmptyOrNull = true;
        }
        return isEmptyOrNull;
    }
}
