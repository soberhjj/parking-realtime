package com.newland.iotshow.util;


public class ToolRunner {

	public static int run(Tool tool, String[] args) throws Exception {
		GenericOptionsParser parser = new GenericOptionsParser(args);
		return tool.run(parser.getConf());
	}

}
