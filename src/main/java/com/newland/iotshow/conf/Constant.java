package com.newland.iotshow.conf;

/**
 * 常量定义
 * 
 * @author Administrator
 *
 */
public interface Constant {
	/**
	 * 数据库连接配置文件
	 */
	String DB_CONF_FILE = "dbconf.properties";
//	String DB_CONF_FILE = "dbtest.properties";

//	// 替换成的新字符/
//	// (字符串被常用于parseObject函数构建json对象,而fastjson对\后面的转义有限制,因此不能用随便的一个字符代替
//	// 比如下面的<br/>,fastjson不支持\后面接<字符,因此在构建json对象时候,当字符串有\<br/>,则抛出异常)
//	// String HTML_BR = "<br/>";
//	String REPLACEMENT_CHAR = "//";
//
//	// 被替换的字符(其实下面的\\\\t在字符串里面就是\\t的意思,因为被转义了)
//	String REGEX_SPECIAL_CHAR = "\t|\r|\n|\\\\t|\\\\r|\\\\n|\001|\002|\003";
//
//	// 数值型的默认值(这个默认值不要随便乱改)
//	int DEFAULT_NUMBER = 0;
//
	/**
	 * 字符串的默认值
	 */
	String DEFAULT_STRING = "null";
//
//	// 默认日期
//	String DEFAULT_DATE = "1970-01-01 08:00:00";
//
//	//默认IP
//	String DEFAULT_IP = "127.0.0.1";
//
	/**
	 * 空字符串
	 */
	String EMPTY = "";

}
